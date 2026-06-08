# Room to Realm Migration Plan

## Reality check
- The app is still fully Room-backed.
- No Realm dependency or plugin is wired yet.
- The new `realmplan` package is intentionally compile-safe scaffolding.
- Room schema versioning is corrected to `37` so device upgrades stop depending on a false database version.

## Why this plan is the safest
- Existing users keep Room as the runtime source of truth until Realm write, validation, and read-path verification are all complete.
- Migration is one-way copy first, never destructive first.
- The old Room database must not be deleted until:
  1. batch copy succeeds
  2. counts validate
  3. hot-path reads validate
  4. the app has shipped with Realm reads enabled and no regression signals

## What is being preserved
- message rows
- thread ids
- message ids
- attachment payloads
- archive/private/block flags
- unread/new-message flags
- pinned state
- scheduled state
- category labels
- recycle bin rows
- reminder rows
- gallery helper tables

## Current schema realities
- `Conversation` is both message storage and thread/UI metadata storage.
- `Conversationbin` is a denormalized recycle-bin copy, not a proper child relation.
- `messagewithattachment` is stored as JSON inside `Conversation` and `Conversationbin`.
- `message_attachments` and `attachments` also exist as standalone tables.
- There are almost no useful SQL indexes except the unique index on `attachments.message_id`.

## Future Realm integration steps
1. Resolve Kotlin/Realm compiler compatibility.
2. Replace `realmplan` markers with real Realm classes and annotations.
3. Implement a real `RealmMigrationStore`.
4. Run Room to Realm copy on launcher/splash in the background.
5. Validate copied counts and selected hot-path query parity.
6. Add a feature flag to switch read-paths from Room to Realm.
7. Keep Room write-through for one release if needed.
8. Delete old Room files only after successful cutover and telemetry confidence.

## Read-path cutover recommendation
- Introduce a `ConversationDataSource` seam first.
- Put current Room access behind `RoomConversationDataSource`.
- Add `RealmConversationDataSource` later.
- Keep a small orchestrating repository that picks the active source.
- Do not expose hundreds of DAO-shaped methods directly from the repository long-term.

## Validation checklist before deleting Room
- conversation count matches
- recycle-bin count matches
- attachment counts match
- random sample of thread ids resolves in Realm
- random sample of message ids resolves in Realm
- archive/private/block flags match
- latest-message ordering by `time` matches
- migration flag stored only after validation succeeds

## Why the Room version bump is no-op safe
- exported schema files `35`, `36`, and `37` in this repo have the same identity hash
- no SQL shape changed between them
- explicit no-op migrations keep upgrade history honest without touching user rows
