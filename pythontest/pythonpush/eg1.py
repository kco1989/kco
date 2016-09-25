# map的例子
# map的作用是将一个函数映射到一个枚举类型上。
l = [1, 2, 3]
a1 = list(map(lambda x: x ** 3, l))
print(a1)
a2 = [x**3 for x in l]
print(a2)

# filter
# filter返回可迭代对象传入function后返回值为True的item。
l = ['H2O', 'HO', 'HNO3', "CO"]
print(list(filter(lambda x: x.isalpha(), l)))
print([x for x in l if x.isalpha()])

# reduce
# reduce是一种类似向左折叠列表的操作，按照可迭代对象的顺序迭代调用函数。
# 并且要求函数接受两个参数。如果有第三个参数，则表示初始值。
from functools import reduce
print(reduce(lambda x, y: x * y, range(1, 10)))

sen = 'I Scream, you scream, we all scream for ice-cream!'
print(reduce(lambda a, x: a + x.count('cream'), sen.split(), 0))
print([i for i in sen.split() if 'cream' in i].__len__())
