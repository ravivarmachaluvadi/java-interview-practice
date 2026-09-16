# Git Commands

Personal Git cheat-sheet: an HTTPS-clone SSL workaround and the feature-branch workflow (branch from `develop`, amend, rebase, force-push) used for raising and updating PRs.

## HTTPS clone without adding the system to GitHub

Without adding the system in GitHub, we can use the HTTPS link to clone by providing username or password. Before or after this we may come across some issue; use the below command.

```bash
git config --global http.sslVerify false
```

## Feature branch workflow: create, push, amend, rebase, force-push

```bash
# 1. Create and checkout a new branch from develop
git checkout -b feature_branch develop

# 2. Make your code changes

# 3. Stage and commit with a message
git add -u
git commit -m "id: message"

# 4. Push branch to origin and set upstream
git push --set-upstream origin feature_branch

# === LATER if you need to update your PR ===

# 5. Stage modified files
git add -u

# 6. Amend last commit without changing its message
git commit --amend --no-edit

# 7. Rebase on top of latest develop
git fetch origin
git rebase origin/develop

# 8. Resolve conflicts if any

# 9. Force push updated commit to feature branch
git push --force

# === OPTIONAL if feature branch is behind PR base branch ===

# 10. Rebase onto updated remote feature branch
git fetch origin
git rebase origin/feature_branch
git push --force
```

A photo of this same workflow is kept at `08-Reference/images/git-feature-branch-rebase-workflow.jpg` ([open image](images/git-feature-branch-rebase-workflow.jpg)).
