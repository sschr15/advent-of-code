package sschr15.aocsolutions.util

// Multi-dimensional iterables
typealias I1d<T> = Iterable<T>
typealias I2d<T> = Iterable<I1d<T>>
typealias I3d<T> = Iterable<I2d<T>>
typealias I4d<T> = Iterable<I3d<T>>
typealias I5d<T> = Iterable<I4d<T>>

typealias L1d<T> = List<T>
typealias L2d<T> = List<L1d<T>>
typealias L3d<T> = List<L2d<T>>
typealias L4d<T> = List<L3d<T>>
typealias L5d<T> = List<L4d<T>>

typealias A1d<T> = Array<T>
typealias A2d<T> = Array<A1d<T>>
typealias A3d<T> = Array<A2d<T>>
typealias A4d<T> = Array<A3d<T>>
typealias A5d<T> = Array<A4d<T>>
