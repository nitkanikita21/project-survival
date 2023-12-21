# Stamina System Specification

## Glossary

Stamina - the amount of time that player can perform stamina-dependant actions like sprinting.

## Variables

- `stamina_time` - amount of stamina time.
- `stamina_remaining` - amount of remaining stamina time.
- `stamina_recovery` - rate of stamina recovery.

## System

While player is sprinting, the `stamina_remaining` decreases until it reaches zero.

When the `stamina_remaining` is zero and player is trying to sprint, he gets slowness effect for specific amount of time.

When the `stamina_remaining` is less than the `stamina_time`, the `stamina_remaining` increases according to the `stamina_recovery` rate.
