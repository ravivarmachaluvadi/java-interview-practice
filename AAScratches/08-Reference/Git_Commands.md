# Git Commands

Personal Git cheat-sheet: cloning over HTTPS when the machine has no SSH key
registered, and the feature-branch workflow (branch from `develop`, amend,
rebase, force-push) used for raising and updating PRs.

## At a glance

| Topic | Where |
| --- | --- |
| Clone over HTTPS, and the TLS error | [HTTPS clone](#https-clone-when-the-machine-has-no-ssh-key) |
| Create a branch, commit, push, raise a PR | [Feature branch workflow](#feature-branch-workflow) |
| Update an open PR safely | [Updating an open PR](#updating-an-open-pr) |
| Commands that are risky or wrong | [Commands to avoid](#commands-to-avoid) |
| One-line lookup of every command here | [Command reference](#command-reference) |

## HTTPS clone when the machine has no SSH key

HTTPS lets you clone without registering an SSH key on the account. Two things
usually trip it up.

**1. Password authentication no longer works.** GitHub stopped accepting account
passwords for Git over HTTPS in August 2021. When Git asks for a password, paste
a **personal access token** instead, or let Git Credential Manager (bundled with
Git for Windows) handle the browser sign-in.

**2. TLS certificate errors on a corporate network.** A proxy that inspects
traffic presents its own certificate, and Git rejects it with
`SSL certificate problem: unable to get local issuer certificate`.

The note this file grew from suggested turning verification off globally. Do not
do that. Options, best first:

| Fix | Command | Scope |
| --- | --- | --- |
| Use the Windows certificate store, which already trusts the corporate root | `git config --global http.sslBackend schannel` | all repos, still verified |
| Point Git at the corporate CA bundle | `git config --global http.sslCAInfo /path/to/ca-bundle.crt` | all repos, still verified |
| Skip verification for one command only | `git -c http.sslVerify=false clone <url>` | that command |
| Skip verification inside one repo | `git config --local http.sslVerify false` | that repo |

`git config --global http.sslVerify false` turns certificate checking off for
**every repository on the machine, permanently**, which means any network you
later join can silently intercept your pushes and pulls. If it is already set,
clear it with `git config --global --unset http.sslVerify`.

## Feature branch workflow

```bash
# 1. Get the current develop, then branch from the REMOTE copy of it
git fetch origin
git switch -c feature_branch origin/develop

# 2. Make your code changes

# 3. Stage and commit
git add -A                       # -A also picks up NEW files; -u would skip them
git commit -m "TICKET-123: short description of the change"

# 4. Push the branch and set its upstream
git push --set-upstream origin feature_branch
```

`git switch -c` is the modern spelling; `git checkout -b feature_branch develop`
does the same thing but needs a local `develop` branch that is already up to
date, which is the usual reason a branch starts life stale.

## Updating an open PR

```bash
# 5. Stage the new edits
git add -A

# 6. Fold them into the existing commit, keeping its message
git commit --amend --no-edit

# 7. Replay your work on top of the latest base branch
git fetch origin
git rebase origin/develop

# 8. If the rebase stops on a conflict
#      edit the file, then:
git add <resolved-file>
git rebase --continue
#      or back out of the whole rebase with:
git rebase --abort

# 9. Publish the rewritten commit
git push --force-with-lease
```

Steps 6 and 7 both **rewrite history**, which is why step 9 has to force. Use
`--force-with-lease`, not `--force`: it refuses the push if someone else has
added a commit to the branch since your last fetch, instead of deleting their
work.

**If someone else pushed to your feature branch,** pick their work up *before*
you amend anything:

```bash
git pull --rebase origin feature_branch
```

## Commands to avoid

| Command | Why | Use instead |
| --- | --- | --- |
| `git config --global http.sslVerify false` | Disables TLS certificate checks for every repo on the machine, forever | `http.sslBackend schannel`, or scope it to one command with `git -c` |
| `git push --force` | Overwrites whatever is on the remote, including commits teammates pushed after your last fetch | `git push --force-with-lease` |
| `git add -u` when you added new files | Stages modifications and deletions of **tracked** files only; brand-new files are silently left out of the commit | `git add -A` |
| `git rebase origin/feature_branch` | Replays your amended commit on top of the **pre-amend** copy of your own branch, duplicating the work and manufacturing conflicts | `git rebase origin/develop` to catch up with the base branch, `git pull --rebase` to pick up someone else's push |
| `git checkout -b feature_branch develop` on a stale clone | Branches from a local `develop` that may be days behind | `git fetch origin && git switch -c feature_branch origin/develop` |

The fourth row is the one to remember. The earlier version of this note ended
with "if the feature branch is behind the PR base branch, `git rebase
origin/feature_branch`". That command does not go anywhere near the base branch
-- it rebases onto the remote copy of the branch you are already on.

## Command reference

| Command | What it does |
| --- | --- |
| `git fetch origin` | Updates the `origin/*` pointers; changes nothing in your working tree |
| `git switch -c <branch> origin/develop` | Creates `<branch>` from the freshly fetched base and checks it out |
| `git add -A` | Stages everything: new, modified and deleted files |
| `git add -u` | Stages modified and deleted **tracked** files only |
| `git commit -m "<msg>"` | Commits the staged changes |
| `git commit --amend --no-edit` | Folds staged changes into the last commit, keeping its message |
| `git push --set-upstream origin <branch>` | First push; also records the tracking branch |
| `git rebase origin/develop` | Replays your commits on top of the latest base branch |
| `git rebase --continue` / `--abort` | Resumes after resolving a conflict / cancels the whole rebase |
| `git push --force-with-lease` | Force-push that refuses to clobber commits you have not seen |
| `git config --global --unset http.sslVerify` | Removes a previously set `sslVerify = false` |

## Original screenshot

The hand-drawn version of this workflow is kept at
`08-Reference/images/git-feature-branch-rebase-workflow.jpg`
([open image](images/git-feature-branch-rebase-workflow.jpg)).
