package net.pursue.utils.Germ;

import lombok.Getter;

@Getter
public enum GermType {
    Germ_1("起床战争", 1),
    Germ_2("空岛战争", 2),
    Germ_3("竞技游戏", 3),
    Germ_4("其他游戏", 4);

    private final String name;
    private final int i;

    GermType(String string, int i) {
        this.name = string;
        this.i = i;
    }
}
