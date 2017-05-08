import winsound

# 定义低音
a1, a2, a3, a4, a5, a6, a7 = 131, 147, 165, 175, 196, 220, 247
# 定义中音
b1, b2, b3, b4, b5, b6, b7 = 262, 296, 330, 349, 382, 440, 494
# 定义高音
c1, c2, c3, c4, c4p, c5, c6, c7 = 523, 587, 659, 698, 741, 784, 680, 988
# 定义高二度
d1, d2, d3, d4, d5, d6, d7 = 1047, 1175, 1319, 1397, 1568, 1760, 1976

oneBeat = 600
falfBeat = 300

schoolSong = [(c5, oneBeat), (c5, oneBeat), (c5, oneBeat), (c5, oneBeat),
              (c5, falfBeat * 3), (c6, falfBeat), (c5, oneBeat * 2),
              (c2, oneBeat), (c2, oneBeat), (c1, oneBeat), (c2, oneBeat),
              (c3, oneBeat * 4),
              (c1, oneBeat), (c3, oneBeat), (c5, oneBeat), (d1, oneBeat),
              (d1, oneBeat * 2), (c7, oneBeat * 2),
              (c6, oneBeat), (c6, oneBeat), (c3, oneBeat), (c4p, oneBeat),
              (c5, oneBeat * 4),
              (c2, oneBeat),(c2, oneBeat), (c5, oneBeat), (c2, oneBeat),
              (c3, falfBeat * 3), (c4, falfBeat), (c3, oneBeat * 2),
              (c5, oneBeat), (c5, oneBeat), (d1, oneBeat), (c5, oneBeat),
              (c6, oneBeat * 4),
              (c6, oneBeat), (c5, oneBeat), (c4, oneBeat), (c5, oneBeat),
              (c6, oneBeat), (c5, oneBeat), (c4, oneBeat), (c5, oneBeat),
              (c6, oneBeat), (c5, oneBeat), (c4, oneBeat), (c3, oneBeat),
              (c2, oneBeat * 4),
              (c1, oneBeat), (c1, oneBeat), (c1, oneBeat), (c1, oneBeat),
              (c7, oneBeat), (c6, oneBeat), (c7, oneBeat), (c1, oneBeat),
              (c2, oneBeat), (c2, oneBeat), (c1, oneBeat), (c2, oneBeat),
              (c3, oneBeat * 4),
              (c5, oneBeat), (c5, oneBeat), (d1, oneBeat), (c7, oneBeat),
              (d1, oneBeat * 2), (c5, oneBeat * 2),
              (c5, oneBeat), (c3, oneBeat), (c2, falfBeat * 3), (c1, falfBeat),
              (c1, oneBeat * 4)]

HappyBirthday = [(c5, falfBeat), (c5, falfBeat), (c6, oneBeat), (c5, oneBeat),
                 (d1, oneBeat), (c7, oneBeat * 2),
                 (c5, falfBeat), (c5, falfBeat), (c6, oneBeat), (c5, oneBeat),
                 (d2, oneBeat), (d1, oneBeat * 2),
                 (c5, falfBeat), (c5, falfBeat), (d5, oneBeat), (d3, oneBeat),
                 (d1, oneBeat), (c7, oneBeat), (d4, falfBeat), (d4, falfBeat),
                 (d3, oneBeat), (d1, oneBeat), (d2, oneBeat),
                 (d1, oneBeat * 2), (c5, falfBeat), (c5, falfBeat),
                 (d5, oneBeat), (d3, oneBeat), (d1, oneBeat),
                 (c7, falfBeat), (c6, oneBeat * 2), (d4, falfBeat),
                 (d4, falfBeat), (d3, oneBeat), (d1, oneBeat), (d2, falfBeat),
                 (d1, oneBeat * 3)]

Legend = [
    #只是因为在人群中多看了你一眼
    (c1, falfBeat), (c1, oneBeat), (c1, oneBeat), (c3, oneBeat), (c2, oneBeat), (c2, falfBeat),
    (c2, falfBeat / 2), (c1, falfBeat), (c1, oneBeat), (c1, falfBeat), (c2, oneBeat), (c2, oneBeat),
    (c1, falfBeat), (b6, falfBeat), (b6, falfBeat / 2), (b6, oneBeat * 2),

    #再也没能忘掉你容颜
    (b7, falfBeat), (b7, falfBeat), (b7, oneBeat), (c1, falfBeat),
    (c2, falfBeat), (c2, oneBeat), (b7, oneBeat), (b6, falfBeat),
    (b5, falfBeat), (b3, falfBeat), (b3, oneBeat * 2),

    #梦想着偶然能有一天再相见
    (c3, falfBeat), (c2, falfBeat), (c3, oneBeat), (c3, falfBeat), (c3, falfBeat / 2),
    (c2, falfBeat), (c2, oneBeat), (c2, falfBeat / 2), (c1, falfBeat), (c1, oneBeat),
    (c2, oneBeat), (b6, oneBeat), (b6, falfBeat), (b6, falfBeat / 2), (c2, falfBeat),
    (c1, falfBeat / 2), (c1, oneBeat * 2),

    #从此我开始孤单思念
    (b7, falfBeat), (b7, falfBeat), (b7, oneBeat), (c1, falfBeat), (c2, falfBeat / 2),
    (c2, oneBeat), (c2, oneBeat), (b6, falfBeat), (b5, oneBeat), (b3, oneBeat * 2),

    #想你时你在天边
    (c5, oneBeat), (c2, falfBeat / 2), (c2, oneBeat), (c3, oneBeat), (c5, oneBeat), (c2, falfBeat),
    (c2, oneBeat), (d1, falfBeat), (b6, oneBeat * 2),

    #想你时你在眼前
    (c2, oneBeat), (b6, falfBeat / 2), (b6, oneBeat), (c3, oneBeat), (c2, oneBeat),
    (c1, falfBeat / 2), (c1, oneBeat), (c1, oneBeat), (b5, oneBeat * 2),

    #想你时你在脑海
    (c5, oneBeat), (c2, falfBeat / 2), (c2, oneBeat), (c3, oneBeat), (c5, oneBeat),
    (c2, falfBeat), (c2, oneBeat), (d1, oneBeat), (b6, oneBeat * 2),

    #想你时你在心田
    (c2, oneBeat), (b6, falfBeat / 2), (b6, oneBeat), (c3, oneBeat), (c2, oneBeat),
    (c1, falfBeat / 2), (c1, oneBeat), (c1, oneBeat), (b5, oneBeat * 2),

    #宁愿相信我们前世有约
    (c1, falfBeat), (c1, falfBeat), (c1, oneBeat), (b5, falfBeat), (c1, falfBeat / 2),
    (c1, oneBeat), (c5, oneBeat), (d4, oneBeat), (c3, oneBeat), (c2, oneBeat),
    (c1, falfBeat / 2), (c1, oneBeat * 2),

    #今生的爱情故事不会再改变
    (c1, falfBeat), (c3, falfBeat), (c5, falfBeat), (c6, oneBeat), (c5, falfBeat),
    (c6, falfBeat / 2), (c6, falfBeat), (c5, oneBeat), (c6, oneBeat), (c5, falfBeat),
    (c3, falfBeat), (c3, falfBeat), (c2, oneBeat), (c3, falfBeat / 2), (c3, oneBeat * 2),

    #宁愿用这一生等你发现
    (c1, falfBeat), (c1, falfBeat), (c1, oneBeat), (b5, falfBeat), (c1, falfBeat / 2),
    (c1, oneBeat), (c5, oneBeat), (d4, oneBeat), (c3, oneBeat), (c2, oneBeat),
    (c1, falfBeat / 2), (c1, oneBeat * 2),

    #我一直在你身边从未走远
    (c1, falfBeat), (c3, falfBeat), (c5, falfBeat), (c6, oneBeat),
    (c5, falfBeat), (c6, falfBeat / 2), (c6, falfBeat), (c5, oneBeat),
    (c6, oneBeat), (c5, falfBeat), (c3, falfBeat / 2), (c5, oneBeat), (c5, oneBeat * 4)
]
for x, y in Legend:
    winsound.Beep(x, int(y))


