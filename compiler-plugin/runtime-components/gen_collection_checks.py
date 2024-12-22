HEADER = '''
@file:OptIn(ExperimentalTypeInference::class)
@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.sschr15.aoc.annotations

import kotlin.experimental.ExperimentalTypeInference
'''.strip()

FUNCTION_BODY = '''
    var sum = 0%s
    for (item in items) {
        sum = Math.addExact(sum, %s.to%s())
    }
    return sum
'''.strip('\n')

TYPED_COLLECTIONS = [
    'Iterable', 'Sequence', 'Array'
]

PRIMITIVES = [
    'Byte', 'Short', 'Int', 'Long'
]

def generate(collection: str, name: str, sum_type: str, generic: bool = False, alt: str | None = None, second_param: str | None = None) -> str:
    output = [
        '@OverloadResolutionByLambdaReturnType' if second_param else None,
        f'@JvmName("{alt}")' if alt else None,
        f'inline fun {"<T> " if generic else ""}{name}(items: {collection}{f", transform: {second_param}" if second_param else ""}): {sum_type} {"{"}',
        FUNCTION_BODY % ('L' if sum_type == 'Long' else '', 'transform(item)' if second_param else 'item', sum_type),
        '}',
    ]
    return '\n'.join([i for i in output if i is not None])

def get_sum_type(typ: str):
    return 'Long' if typ == 'Long' else 'Int'

if __name__ == '__main__':
    declarations = []

    for prim in PRIMITIVES:
        for typ in TYPED_COLLECTIONS:
            declarations.append(generate(f'{typ}<{prim}>', 'sum', get_sum_type(prim), alt=f'sumOf{prim}s'))

        declarations.append(generate(f'{prim}Array', 'sum', get_sum_type(prim)))

        for p2 in PRIMITIVES:
            declarations.append(generate(f'{prim}Array', 'sumOf', get_sum_type(p2), alt=f'sumOf{prim}sTo{p2}s', second_param=f'({prim}) -> {p2}'))

        declarations.append(generate('CharArray', 'sumOf', get_sum_type(prim), alt=f'sumOfCharsTo{prim}s', second_param=f'(Char) -> {prim}'))

    for typ in TYPED_COLLECTIONS:
        for prim in PRIMITIVES:
            declarations.append(generate(f'{typ}<T>', 'sumOf', get_sum_type(prim), generic=True, alt=f'sumTo{prim}s', second_param=f'(T) -> {prim}'))

    output_file = [
        HEADER,
        *declarations,
    ]

    with open('src/main/kotlin/CollectionOverflowChecks.kt', 'wb') as file:
        file.write('\n\n'.join(output_file).encode())
