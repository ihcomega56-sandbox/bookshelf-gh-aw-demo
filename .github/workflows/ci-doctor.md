---
description: |
  CI（Gradle ビルド）が失敗したときに、ログを解析して原因の仮説と対処方針を
  Issue として起票するワークフロー。

on:
  workflow_run:
    workflows: ["CI"]
    types: [completed]
    branches: [main]

if: ${{ github.event.workflow_run.conclusion == 'failure' }}

permissions: read-all

network: defaults

tools:
  github:
    toolsets: [actions, issues, repos]
  cache-memory: true

safe-outputs:
  create-issue:
    title-prefix: "[ci-doctor] "
    labels: [ci, automation]
    max: 1

timeout-minutes: 15
---

# CI ドクター

`CI` ワークフロー（`./gradlew build`）が main ブランチで失敗しました。原因を調査してください。

## 手順

1. 失敗したワークフロー実行のジョブとログを取得する。
2. 失敗したステップと、Gradle / JUnit のエラーメッセージを特定する。
3. 該当するソースコード（`src/main/java`、`src/test/java`、`build.gradle`）を確認する。
4. 過去に同じ失敗が起きていないか、`cache-memory` に保存した情報や既存 Issue を確認する。
5. 調査結果を Issue として起票する。

## Issue に含める内容

- **概要**: 何が失敗したかを 1〜2 行で
- **失敗したステップ**: ステップ名と該当ログの抜粋（20 行以内）
- **原因の仮説**: 根拠となるコード箇所を添えて記載する。確信度（高／中／低）も明示する
- **対処方針**: 具体的な修正案。複数ある場合は推奨順に並べる
- **再発防止**: 追加すべきテストやチェックがあれば記載する

## 注意事項

- ログの秘匿情報（トークンなど）は絶対に転記しない。
- 原因が特定できない場合は、無理に断定せず「追加調査が必要な点」を列挙する。
- 出力はすべて日本語で記述する。
