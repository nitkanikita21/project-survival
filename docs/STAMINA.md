# Stamina System Specification

## Glossary

Stamina - amount of time that player can perform stamina-dependant actions like sprinting.

## Variables

- `stamina_time` - amount of stamina time.
- `stamina_remaining` - amount of remaining stamina time.
- `stamina_recovery` - rate of stamina recovery.
- `stamina_recovery_block` - flag that indicates whether to block stamina recovery.
- `stamina_slowness` - amount of time when player slowed down.

## System

While player is sprinting, `stamina_remaining` decreases until it reaches zero.

When `stamina_remaining` is zero and player is trying to sprint, he gets slowness effect for `stamina_slowness` time.

When `stamina_remaining` is less than `stamina_time`, `stamina_remaining` increases according to `stamina_recovery` rate.
This rule is disabled when `stamina_recovery_block` flag is active.
