# summon several angry bees and a couple vexes at this location
# TODO not targetting the player...
summon minecraft:bee ~ ~ ~ {Anger:2000}
summon minecraft:bee ~ ~ ~ {Anger:2000}
summon minecraft:bee ~ ~ ~ {Anger:2000}
summon minecraft:vex ~ ~ ~ {LifeTicks:800}
summon minecraft:vex ~ ~ ~ {LifeTicks:800}
# execute positioned ~ ~ ~ as @e[type=bee,distance=..16] store result entity @s HurtBy[0] double 1 run data get entity @p[distance=..16] UUID[0]
# execute positioned ~ ~ ~ as @e[type=bee,distance=..16] store result entity @s HurtBy[1] double 1 run data get entity @p[distance=..16] UUID[1]
# execute positioned ~ ~ ~ as @e[type=bee,distance=..16] store result entity @s HurtBy[2] double 1 run data get entity @p[distance=..16] UUID[2]
# execute positioned ~ ~ ~ as @e[type=bee,distance=..16] store result entity @s HurtBy[3] double 1 run data get entity @p[distance=..16] UUID[3]