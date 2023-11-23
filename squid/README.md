This folder, `jmh`, is a copy of [this excellent benchmark suite by Diego Elias Costa](https://github.com/DiegoEliasCosta/CollectionsBench),
with small changes to avoid needing Git LFS (this doesn't use his LFS either).


`java -jar benchmarks.jar "Pathfinding.+doPath" -wi 5 -i 5 -f 1 -w 10 -r 10`