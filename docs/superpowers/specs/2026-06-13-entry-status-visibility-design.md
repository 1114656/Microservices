# Entry Status Visibility Design

## Goal

Diary entries and blog articles support two independent concepts:

- `status`: content lifecycle, where `0` means draft and `1` means published.
- `visibility`: audience scope, where `0` means owner only, `1` means logged-in users, and `2` means public.

## Access Rules

- `GET /diary/page` and `GET /blog/page` are public endpoints and can be called without a token.
- Anonymous page queries return only published public entries: `status = 1` and `visibility = 2`.
- Logged-in page queries return the user's own entries regardless of status or visibility, plus published entries from other users with `visibility` of logged-in users or public.
- Detail, create, update, and delete endpoints continue to require login. Diary write endpoints continue to require their existing permission codes.

## Defaults

- New diary entries default to `status = 1` and `visibility = 1` when the frontend does not pass values.
- New blog articles default to `status = 1` and `visibility = 1` when the frontend does not pass values.

## Data Model

- Add `status` and `visibility` to `diary_entry`.
- Keep `blog_article.status` and add `visibility` to `blog_article`.
- Request, response, and page query VOs expose both fields.

