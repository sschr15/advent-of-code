# AoC Kotlin Compiler Plugin

## 0.9.0

- Fix `sum` replacements to not target `Float` and `Double` types

## 0.8.0

- Add (approximate) locations to compiler plugin errors
  - This may be inaccurate in some cases but will remain close to the actual location.
  - If reporting bugs, include the surrounding context and if possible the IR of the erroring function.
- Modify `rem` checks to only warn once during the JVM's lifetime.
  - To manually disable the warning entirely, pass `-Daoc.warnOnNegativeRemainder=false` to the JVM.

## 0.7.0

- Add Iterable/Sequence/Array summation overflow checks
  - This will replace `sum` and `sumOf` calls with inlined alternatives.
  - `sumBy` (deprecated in Kotlin) is mapped to `sumOf` before being replaced.
    If using `sumBy`, the plugin will raise a warning.

## 0.6.0

- *Actually* fix memoization on high-arity functions (varargs are hard)

## 0.5.0

- Modify destructuring errors to be a little less hard to read
- Fix memoization on extension functions and high-arity functions

## 0.4.0

- Add runtime errors for collection destructuring matching an incorrect number of elements
  - Currently only checks variable assignment, not lambda parameter destructuring
  - Can be skipped on any function or child of a function or class with `@SkipDestructuringChecks`

## 0.3.0

- Move `@SkipOverflowUnderflowChecks` from `sschr15.aoc.annotations` to `com.sschr15.aoc.annotations`
- Ensure only operations with overflow checks are compared against
  (Fixes a bug where custom overloads would be incorrectly matched)

## 0.2.0

- Move annotations from `sschr15.aoc.annotations` to `com.sschr15.aoc.annotations`
- Convert all annotations to source retention

## 0.1.0

- Initial Release
- Add `@Memoize` annotation
  - Memoizes any annotated function using a `Map` based on function inputs
- Add integer and long overflow checks
  - Wraps all `+`, `-` and `*` operators; `++` and `--` postfix operators;
    `-` unary minus; and absolute value checks, throwing an error if any
    cause an overflow
  - Wraps all `%` operators, raising a warning if the first operand is negative
  - Ensures conversions are within the range that can be exactly
    represented by the given type, erroring when there is not enough precision
    - Errors if `toFloat` is attempted outside the range -16,777,216 to 16,777,216
    - Errors if `toDouble` is attempted outside the range -9,007,199,254,740,992 to 9,007,199,254,740,992
    - Errors if `toInt` on a `Long` is attempted outside the range -2,147,483,648 to 2,147,483,647
  - Add `@SkipOverflowUnderflowCheck` annotation to avoid doing these checks
- Add `@ExportIr` annotation
  - During compilation, sends compiler warnings containing the IR of the annotated functions
