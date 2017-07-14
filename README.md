Black Diamonds: Reusable Components for Truffle Languages
=========================================================

<img src="https://github.com/SOM-st/black-diamonds/raw/master/docs/sliced-black-truffle-by-mortazavifar-ccbysa40.jpg" align="right" width="200" height="170" alt="Sliced Black Truffles (By Mortazavifar, CC BY-SA 4.0)" title="Sliced Black Truffles (By Mortazavifar, CC BY-SA 4.0)">

The goal of this project is to build a collection of reusable components for
Truffle language implementers.

Ideally, the components remain somewhat independent of each other, and
independent of specific language semantics. However, because of the way they
are developed, there is a certain bias towards languages with simple, uniform,
and consistent semantics.

Thus, the black diamonds might not be applicable directly to your language
implementation. We are happy to further generalize the components and include
changes, but sometimes, things are too different, and it might be easier to
devise a simpler solution specific to a language.


Guidelines and Principles
=========================

 - designed to build the "right" thing for today, not to defend against tomorrow
 - there is no backwards compatibility
 - keep things simple
 - to support research
 - the guidelines can change at any point in time 


License and Acknowledgements
============================

This project heavily relies on the [Truffle] framework developed by Oracle Labs.

The included components are developed in the context of [TruffleSOM], [SOMns], [TruffleMate], and [Grace (SOMns)][grace].

Information on previous authors are included in the AUTHORS file. This code is
distributed under the MIT License. Please see the LICENSE file for details.

[Truffle]: https://github.com/graalvm/graal/tree/master/truffle
[TruffleSOM]: https://github.com/SOM-st/TruffleSOM
[SOMns]: https://github.com/smarr/SOMns
[TruffleMate]: https://github.com/charig/TruffleMate
[grace]: https://github.com/richard-roberts/SOMns/tree/temporary-fixes-for-execution
