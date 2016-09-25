import itertools


for item in itertools.chain('ABC', 'DEF'):
    print(item)

for x, y, z in itertools.combinations('ABCD', 3):
    print(x + y + z)