Matrix4 Comparative Benchmark
--

Benchmarking
[libGDX 1.9.11-SNAPSHOT from January 7 2020](https://github.com/libgdx/libgdx/blob/82d9147710c4534b7d4dab32b70a203e1060e85c/gdx/src/com/badlogic/gdx/math/Matrix4.java)
([See also the native code](https://github.com/libgdx/libgdx/blob/82d9147710c4534b7d4dab32b70a203e1060e85c/gdx/jni/com.badlogic.gdx.math.Matrix4.cpp)) against
[Arc, also from January 7 2020](https://github.com/Anuken/Arc/blob/9f70d5a39a910bd855430d74e41d0b8753b47442/extensions/g3d/src/arc/math/geom/Matrix4.java).

Benchmarks were run on a laptop with an `i7-6700HQ` processor at 2.60 GHz, and enough available DDR4 memory to run the benchmarks without issue.
The installed OS is `Windows 7 SP 1`, and the Java version is
`OpenJDK 64-Bit Server VM (AdoptOpenJDK)(build 25.212-b03, mixed mode)` (HotSpot, Java 13).
All results were given by BumbleBench in a Maven project (a copy of the BumbleBench repo).
Higher Ops/second are better; leading zeroes may be present to account for HTML formatting.

Function | Source | Ops/second
-------- | ------ | ------------
mul      | GDX    | `05816800.5`
mul      | Arc    | `19658630.0`
inv      | GDX    | `06431694.5`
inv      | Arc    | `10316652.0`
rot      | GDX    | `06755105.5`
rot      | Arc    | `58535096.0`
det      | GDX    | `09183376.0`
det      | Arc    | `40342672.0`

Note, the Arc code does nothing out of the ordinary; it is pure Java, whereas the libGDX Matrix4 code uses JNI.
Yet, the Matrix4 in pure Java outperforms the JNI code without fail.
The pure-Java implementations are between 1.60403327614519 and 8.665311888911283 times faster than the JNI ones, averaging
4.5104963978124495 times faster across four benchmarks.

This BumbleBench benchmark can't be run on Android, but a different benchmark written by Anuken (author of Arc) showed similar
results to this benchmark when run on desktop, and a more extreme advantage for pure-Java on Android:

Anuken's Matrix4 benchmark, desktop (JVM/hardware unknown)

```
--MUL--
Native: 534.8446
Java:   154.74403
--INV--
Native: 622.4105
Java:   533.11163
--ROT--
Native: 409.207
Java:    47.07709
--DET--
Native: 390.66953
Java:   125.57833
```
Anuken's benchmark, Android (version/hardware unknown)

```
--MUL--
Native: 1504.5692
Java:    208.19531
--INV--
Native: 1082.6859
Java:    695.11566
--ROT--
Native: 1404.1995
Java:     58.35052
--DET--
Native: 820.7786
Java:   150.43698
```