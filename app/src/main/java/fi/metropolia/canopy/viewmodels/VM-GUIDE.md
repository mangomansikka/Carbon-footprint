## The purpose of viewmodels package

The **VM** of MVVM. The bridge between UI and domain logic.

- UI state management
- Holds screen state
- Transforms domain data into UI-friendly formats
- Survives configuration changes

**IMPORTANT**
- No Android UI dependencies
- No direct access to sensors or databases
- Talks to domain layer only
- Does not store data permanently