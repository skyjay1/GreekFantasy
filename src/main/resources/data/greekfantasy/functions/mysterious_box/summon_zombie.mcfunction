# summon zombies and give player poison effect
summon minecraft:zombie ~ ~ ~ {ArmorItems:[{},{},{},{Count:1,id:leather_helmet,tag:{display:{color:6192150}}}]}
summon minecraft:zombie ~ ~ ~ {ArmorItems:[{},{},{},{Count:1,id:leather_helmet,tag:{display:{color:16701501}}}]}
effect give @p poison 12 0
summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:splash_potion",Count:1,tag:{Potion:"minecraft:long_poison"}}}
summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:splash_potion",Count:1,tag:{Potion:"minecraft:long_poison"}}}
summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:potion",Count:1,tag:{Potion:"minecraft:strong_poison"}}}
summon minecraft:item ~ ~ ~ {Item:{id:"minecraft:rotten_flesh",Count:11}}