def cheeseshop(kind, *argu, **keywords):
    print("你有任意", kind, "?")
    print("对不起,我们都不是", kind)
    for arg in argu:
        print(arg)
    print("-" * 40)
    keys = sorted(keywords.keys())
    for kw in keys:
        print(kw, ": ", keywords[kw])



cheeseshop("Limburger", "It's very runny, sir.",
           "It's really very, VERY runny, sir.",
           shopkeeper="Michael Palin",
           client="John Cleese",
           sketch="Cheese Shop Sketch")