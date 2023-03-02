package com.phantom.originiumarts.common.weather

/**
 *  STORM(风暴) RAINSTORM(暴雨) BLIZZARD(暴雪) SANDSTORM(沙暴) METEOR_SHOWERS(陨石雨)
 *
 *  地形↓ 强度等级→ |   1   2   3   4   5   6   7   8
 *  ———————————————————————————————————————————————
 *       干燥     |  沙暴----------------陨石雨------
 *       湿润     |  风暴-----暴雨--------陨石雨------
 *       寒冷     |  风暴-----暴雪--------陨石雨------
 *
 */
enum class CatastropheType {
    NULL,
    STORM,
    SANDSTORM,
    RAINSTORM,
    BLIZZARD,
    METEOR_SHOWERS;
}