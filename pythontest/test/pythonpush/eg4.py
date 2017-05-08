with open(r'../poem.txt') as fp:
    for line in fp:
        print(line, end=" ")

print("=" * 40)
# 类似于:
fp = open(r'../poem.txt')
try:
    for line in fp:
        print(line, end=' ')
finally:
    fp.close()
print("=" * 40)


# 自定义上下文管理器
class DummyResource:
    def __init__(self, tag):
        self.tag = tag
        print('Resource [%s]' % tag)

    def __enter__(self):
        print('[Enter %s]: Allocate resource.' %self.tag)
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
       print('[Exit %s]:Free resource' %self.tag)
       if exc_tb is None:
           print('[Exit %s]: Exited without exception.' %self.tag)
       else:
           print('[Exit %s]: Exited with exception raised.' %self.tag)
       return False

with DummyResource('Normal'):
    print('[with-body] Run without exception')
print("=" * 40)

with DummyResource('with-exception'):
    print('[with-body] run with exception.')
    raise Exception
    print('[with-body] run with exception,Failed to finish statement-body.')
print("=" * 40)
