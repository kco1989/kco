def f(*args, **kwargs):
    print(args,kwargs)
    print("=" * 40)

l = [1, 2, 3]
t = (4, 5, 6)
d = dict(zip(set('abc'), set('789')))
f()
f(1, 2, 3)
f(1, 2, 3, 'groovy')
f(a=1, b=2, c=3)
f(a=1, b=2, c=3, zzz="hi")
f(1, 2, 3, a=1, b=2, c=3)
f(*l, **d)
f(*t, **d)
f(1, 2, *t)
f(q='winning', **d)
f(1, 2, *t, q="winning", **d)


def f2(arg1, arg2, *args, **kwargs):
    print(arg1,arg2,args,kwargs)
    print("*" * 40)

f2(1, 2, 3)
f2(1, 2, 3, 'groovy')
f2(arg1=1, arg2=2, c=3)
f2(arg1=1, arg2=2, c=3, zzz="hi")
f2(1, 2, 3, a=1, b=2, c=3)
f2(*l, **d)
f2(*t, **d)
f2(1, 2, *t)
f2(q='winning', **d)
f2(1, 2, *t, q="winning", **d)

