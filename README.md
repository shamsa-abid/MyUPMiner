## Welcome to MyUPMiner
This is a project Ive started as part of my reimplmentation of a research tool called UPMiner. Its purpose is to recommend API usage sequences after mining API usages from source code


For more details see [Mining succinct and high-coverage API usage patterns from source code](https://dl.acm.org/citation.cfm?id=2487146).

### Abstract

During software development, a developer often needs to discover specific usage patterns of Application Programming Interface (API) methods. However, these usage patterns are often not well documented. To help developers to get such usage patterns, there are approaches proposed to mine client code of the API methods. However, they lack metrics to measure the quality of the mined usage patterns, and the API usage patterns mined by the existing approaches tend to be many and redundant, posing significant barriers for being practical adoption. To address these issues, in this paper, we propose two quality metrics (succinctness and coverage) for mined usage patterns, and further propose a novel approach called Usage Pattern Miner (UP-Miner) that mines succinct and high-coverage usage patterns of API methods from source code. We have evaluated our approach on a large-scale Microsoft codebase. The results show that our approach is effective and outperforms an existing representative approach MAPO. The user studies conducted with Microsoft developers confirm the usefulness of the proposed approach in practice.

### Architecture

![Image of UPMiner Architecture](https://github.com/shamsa-abid/MyUPMiner/blob/master/Architecture.PNG)




### References

Wang, J., Dang, Y., Zhang, H., Chen, K., Xie, T., & Zhang, D. (2013, May). Mining succinct and high-coverage API usage patterns from source code. In Proceedings of the 10th Working Conference on Mining Software Repositories (pp. 319-328). IEEE Press.
