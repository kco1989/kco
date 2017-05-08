from collections import Counter
import json


some_list = [1, 2, 3, 4, 5]
another_list = [x + 1 for x in some_list]
even_set = {x for x in some_list if x % 2 == 0}
print(even_set)
d = {x: x % 2 ==0 for x in range(1,11)}
print(d)

c = Counter('hello world')
print(c)
print(c.most_common(2))


data = {"status": "OK", "count": 2, "results": [{"age": 27, "name": "Oz", "lactose_intolerant": True}, {"age": 29, "name": "Joe", "lactose_intolerant": False}]}
print(json.dumps(data))
print(json.dumps(data, indent=2))