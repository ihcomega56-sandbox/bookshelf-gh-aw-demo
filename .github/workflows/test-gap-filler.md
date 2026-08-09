---
description: |
  Issue や PR で `/add-tests` とコメントすると、不足しているテストを補う
  プルリクエストを作成するワークフロー。

on:
  slash_command:
    name: add-tests
    events: [issues, issue_comment, pull_request_comment]
  reaction: eyes

permissions: read-all

network:
  allowed:
    - defaults
    - java

tools:
  github:
    toolsets: [issues, pull_requests, repos]
  bash: true
  edit:

safe-outputs:
  create-pull-request:
    title-prefix: "[add-tests] "
    labels: [test, automation]
    draft: true
    max: 1
    allowed-files:
      - "src/test/**"
  add-comment:
    max: 1

timeout-minutes: 20
---

# テスト追加エージェント

`/add-tests` が実行されました。不足しているテストを追加するプルリクエストを作成してください。
コメント本文に対象（クラス名や観点）の指定があれば、それを最優先で扱ってください。

## 手順

1. 対象クラスの実装（`src/main/java`）と既存テスト（`src/test/java`）を読み、テストされていない分岐を特定する。
   - 特に `BookshelfService` の貸出上限（1 人 5 冊まで）と延滞チェックはテストが不足している。
2. 既存テストの書き方（`@SpringBootTest`、AssertJ、日本語のテストメソッド名）に合わせてテストを追加する。
3. `./gradlew test` を実行し、追加したテストが通ることを確認する。失敗した場合はテストを修正する。
   - 実装のバグを見つけた場合は、テストを緩めずに Issue へのコメントで報告する。
4. プルリクエストを作成する。

## 制約

- 変更してよいのは `src/test/**` のみ。実装コード（`src/main/**`）は変更しない。
- 既存のテストを削除・変更しない。追加のみ行う。
- テストメソッド名・コメント・PR 説明はすべて日本語で記述する。

## PR 説明に含める内容

- 追加したテストの一覧と、それぞれが検証している仕様
- テストを追加した根拠（どの分岐が未検証だったか）
- `./gradlew test` の実行結果
