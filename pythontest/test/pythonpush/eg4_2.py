from contextlib import contextmanager,closing


@contextmanager
def demo():
    print('[Allocate resource]')
    print('Code before yield-statement executes in __enter__')
    yield '*** contextmanager demo ***'
    print('code after yield-statement execute in __exit__')
    print('[Free resources]')

with demo() as value:
    print('Assigned Value:%s' % value)
print("=" * 40)


class ClosingDemo(object):
    def __init__(self):
        self.acquire()

    def acquire(self):
        print('Acquire resources')

    def free(self):
        print('clean up any resources acquired.')

    def close(self):
        self.free()

with closing(ClosingDemo()):
    print('using resource')

