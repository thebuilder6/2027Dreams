---
title: Design Philosophy
audience: [human, ai]
owner: programming-leads
last_verified: 2026-10-07
status: authoritative
---

# Design philosophy — what carries into advanced features

Distilled from 2026Dreams (`TitanRoboticsBuildSeason`, 467 tests green 2026-10-06). Each principle names the seam that makes it real.

1. **Game-agnostic core, game-specific leaves.** Generic code takes a `GameDefinition`; only `Game/` knows field targets. Advanced features (auto-choosers, decision engines, score rigs) consume the interface, so kickoff adds a class instead of rewriting the robot.
2. **Single owner per fact.** Ports → `PortMap`. Field dims → `FieldMap` ← `GameDefinition`. Alliance mirroring → `AllianceFlipUtil`. Zone edges, RPM tables, path margins each get one owner. 2026 drift bugs (hardcoded `4.60` vs owned `4.6256`, parallel Blue/Red literals) are impossible by construction.
3. **Blue-origin geometry.** Define once for Blue, derive Red (`X_red = LEN − X_blue`, heading `180° − θ`). Tested in `AllianceFlipUtilTest`.
4. **Fail-safe defaults.** Empty/unknown sensor state degrades to hold/stop, never to a confident wrong action. 2026 lesson: an empty knowledge matrix fell through to `RUSH_CLIMB` for a defender — the 2027 base requires explicit no-information paths.
5. **Isolate faults, throttle noise.** `SubsystemManager` contains one bad subsystem; `Alert` rate-limits. No console spam in the driver loop.
6. **Hardware/sim parity behind IO.** Real vs sim behind small `*IO` interfaces from day one, even before AdvantageKit lands. Physics never leaks into subsystem logic.
7. **Measure before tuning.** Score rigs, decision cards, and replay come before constant changes. Seams: seeded determinism helper + per-bot attribution + JSONL-style match rows (to be re-added with the 2027 sim stack).
8. **Humans and agents share one map.** `AGENTS.md` + `docs/INDEX.md` + topic guides + `CHANGELOG.md` with test evidence. If it isn't in the map, it doesn't exist.
