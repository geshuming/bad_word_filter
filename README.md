# Bad Word Filter (for Japanese text)

This project serves as a benchmark test for an improved implementation of a bad word filter for japanese text. Both the
old implementation and new implementation are available in this project.

The requirements are as follows:

- An input list of bad words to filter:
    - The number of bad words is at least 5,000-10,000.
    - Each word is around 2-20 characters long.
    - Almost all words consist of Japanese characters.
- The text to apply the filter:
    - The number of texts to filter is at least 250,000,000 (can be parallelized).
    - The text is around 500-5,000 characters long.
    - The text consists mostly of Japanese characters.
- For each text:
    - Find the first instance of a bad word and return true if found, otherwise false.
    - The bad word may only apply to a list of included genres, or the bad word is applied to all but a list of excluded
      genres.

## Benchmark Results
Overall, there is a reduction of around 90-95% in the test durations. On my Production Spark Environment, the stage that does the bad word filtering also sees a reduction from 3ms per record to 0.15ms, a whopping 95% reduction!

See [Benchmark Results](docs/BenchmarkResults.md) for more results.

## Implementation

The new implementation takes inspiration from [the Word Search problem](https://leetcode.com/problems/word-search/).
We can utilize the Trie data structure to optimize the search function.

Assuming the text has **m** characters, and the number of bad words is **n** with average character length of **p**.

In the old implementation, we use a naive algorithm with a time complexity of at least **O(m\*n\*p)**:

```
for (badWord <- badWords) { str.contains(badWord) }
```

In the new implementation, we simply need to search the Trie for our bad word, giving us a time complexity of **O(
m\*log(p))**.

```
def checkTrie(trie, str) = { for (i <- str.length) { trie.find(str(i)).fold(nextTrie => checkTrie(nextTrie, str.substring(i))) }
```

## Limitations and Improvements

There are many possible enhancements to the word filter that can be made.

### Functional Improvements

- Provide more details (e.g. index of word) and possibly allow string replacements
- Instead of includeGenres and excludeGenres, we can implement a generic Rule class
    - E.g. Ignore bad word if genre is in x
    - E.g. Ignore bad word if genre is not in x
    - E.g. Ignore bad word if tags include x
- Implement tokenization (english/japanese) to improve word matching
- Implement fuzzy matching (for english bad words)
- Implement character classes (e.g. whitespace character class for spaces)
- Implement hot reload for input list

### Non-Functional Improvements

- Only supports UTF-8, may include support for SHIFT-JIS
- Provide memory benchmarks
- Provide better metrics from Spark
