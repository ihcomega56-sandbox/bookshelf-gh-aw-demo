---
description: |
  依存ライブラリの既知の脆弱性やセキュリティ上の懸念を毎週チェックし、
  対応方針をまとめた Issue を作成するワークフロー。

on:
  schedule: weekly
  workflow_dispatch:

permissions: read-all

network:
  allowed:
    - defaults
    - java

tools:
  github:
    toolsets: [issues, repos, code_security]
  bash: true
  web-fetch:

safe-outputs:
  create-issue:
    title-prefix: "[security] "
    labels: [security, automation]
    max: 1
    close-older-issues: true

timeout-minutes: 20
---

# セキュリティレビュー

Java / Spring Boot 製の蔵書管理アプリ「bookshelf」のセキュリティ上の懸念を洗い出してください。

## 手順

1. `build.gradle` を読み、依存ライブラリと Spring Boot のバージョンを把握する。
2. `./gradlew dependencies --configuration runtimeClasspath` で実際に解決されるバージョンを確認する。
3. Dependabot アラートや Code scanning アラートが有効な場合は、その内容を取得する。
4. 主要な依存ライブラリについて、既知の脆弱性（CVE / GHSA）が該当バージョンに存在しないかを確認する。
5. アプリケーションコードについても、以下の観点で確認する。
   - 入力値のバリデーション漏れ（`web/dto` 配下）
   - 認証・認可の欠如（現状 `borrower` をリクエストからそのまま受け取っている）
   - エラーレスポンスからの情報漏えい
   - 機密情報のハードコード

## Issue に含める内容

- **サマリ**: 検出件数と全体のリスク評価
- **一覧表**: `| 深刻度 | 対象 | 内容 | 推奨対応 |` の Markdown テーブル
- **依存の更新案**: 更新すべきバージョンと、破壊的変更の有無
- **すぐに対応すべきもの / 中長期で対応するもの** の切り分け

## 注意事項

- 脆弱性の有無を推測で断定しない。確認できた根拠（アドバイザリ ID や URL）を必ず添える。
- 実際の攻撃コード（PoC）は記載しない。
- 出力はすべて日本語で記述する。
