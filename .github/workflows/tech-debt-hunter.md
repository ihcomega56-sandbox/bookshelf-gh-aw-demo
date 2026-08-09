---
description: |
  リポジトリ内の TODO コメントや技術的負債を毎週棚卸しし、
  優先度付きのレポート Issue を作成するワークフロー。

on:
  schedule: weekly
  workflow_dispatch:

permissions: read-all

network: defaults

tools:
  github:
    toolsets: [issues, repos]
  bash: true

safe-outputs:
  create-issue:
    title-prefix: "[tech-debt] "
    labels: [refactoring, automation]
    max: 1
    close-older-issues: true

timeout-minutes: 15
---

# 技術的負債の棚卸し

Java / Spring Boot 製の蔵書管理アプリ「bookshelf」の技術的負債を棚卸しし、レポート Issue を作成してください。

## 手順

1. `grep -rn "TODO" src` などでソースコード中の TODO / FIXME コメントを収集する。
2. 収集した各項目について、実際のコードを読んで内容と影響範囲を把握する。
3. 併せて以下の観点でリファクタリング候補を洗い出す。
   - 1 クラス・1 メソッドに責務が集中していないか（例: `BookshelfService`）
   - マジックナンバーやビジネスルールがハードコードされていないか
   - N+1 クエリやページングの欠如などパフォーマンス上の懸念がないか
   - テストが不足しているビジネスルールがないか
4. 既に対応中の Issue や PR がないかを検索し、重複を避ける。

## Issue に含める内容

- **サマリ**: 検出した項目数と全体所見
- **一覧表**: `| 優先度 | 対象ファイル:行 | 内容 | 提案する対応 |` の Markdown テーブル
- **優先度の根拠**: 影響範囲・実装コスト・リスクの観点で説明する
- **今週着手するなら**: おすすめの 1〜3 件と、その理由

## 注意事項

- 実在するファイルパスと行番号のみを記載する（推測で書かない）。
- コードの修正や PR 作成は行わない。レポートのみを作成する。
- 出力はすべて日本語で記述する。
