# Skill system specification

## Glossary

Skill - a player's skill that he can improve throughout the game until its level becomes maximum.

## Variables

### Skill properties

- `maxLevel` - maximum level for this skill
- `baseExpGrowth` - the base experience increment number of this skill
- `expGrowthModifier` - experience gain modifier for each level of this skill
- `baseMaxExpAmount` - base maximum amount of experience of this skill
- `maxExpAmountModifier` - a modifier for the maximum experience gain per level of this skill

### Skill data

- `currentLevel` - current skill level
- `currentExpAmount` - the amount of experience at the moment

## System

The system consists of a set of unique skills for each player that do not depend on each other.
Each of these skills has personal leveling parameters that may differ from other skills.
The skill has constant parameters _(Skill properties)_ and dynamic variables _(Skill data)_ that can change

If an event has occurred that can increase the level of the skill, then the number
calculated by the formula `baseExpGrowth * (expGrowthModifier * currentLevel)` is added to `currentExpAmount`. If after
adding experience `currentExpAmount` is greater than `baseMaxExpAmount * (maxExpAmountModifier * (currentLevel))` then
the
difference between the numbers is written in `currentExpAmount` and `currentLevel` is increased by one

If after calculations `currentLevel >= maxLevel` then `currentExpAmount` is set to zero and further calculations of level
and experience for this skill do not take place (because the skill has reached the maximum level)