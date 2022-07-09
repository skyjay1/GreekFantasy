# summon several angry bees and a couple vexes at this location
summon minecraft:bee ~ ~ ~ {AngerTime:3000}
summon minecraft:bee ~ ~ ~ {AngerTime:3000}
summon minecraft:bee ~ ~ ~ {AngerTime:3000}
execute as @e[type=minecraft:bee,distance=0..4] at @s run data modify entity @s AngryAt set from entity @p UUID
summon minecraft:vex ~ ~ ~ {LifeTicks:800}
summon minecraft:vex ~ ~ ~ {LifeTicks:800}
