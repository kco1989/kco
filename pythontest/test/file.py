# f = open("tmp.txt", "r+")
# print(f.read())
# f.seek(0)
# for line in f:
#     print(line, end='')
# f.write('This is a test\n')
# f.close()

# f = open("tmp2.txt","rb+")
# f.write(b"0123456789abcdef")
# f.seek(5)
# print(f.read(1))
# print(f.seek(-3,2))
# print(f.read(1))

import pickle


def hello():
    print("hello world")

s = [x for x in range(20)]
f = open("tmp3.txt", "wb")
pickle.dump(hello, f)
f.close()
f = open("tmp3.txt", "rb")
x = pickle.load(f)
x()
f.close()
