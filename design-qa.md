# Design QA

final result: passed

Checked target: Product Design option 1, "Canopy Documentary", revised so the hero fully occupies the first viewport and diary content appears only after scrolling.

Local preview: http://127.0.0.1:5175/diary

Screens checked:
- `/diary` first viewport: full-screen rainforest documentary hero renders, generated green planet image asset loads, transparent navigation overlays the hero, no diary content is visible above the fold.
- `/diary` after scrolling: navigation transitions to a deep green translucent glass bar, diary section begins at the top of the viewport, refined empty state renders when no local public diary data is available.
- `/diary` animation pass: hero title, subtitle, actions, and observation rail finish their staggered title-card entrance; the hero image layer shifts subtly during scroll; the diary section fades in after it enters the viewport.
- `/diary` side bloom pass: the diary content area uses green-planet-palette foliage layers from the same hero asset, emerging from the left and right edges as the user scrolls down and retreating when scrolling back up.
- Reduced-motion pass: when `prefers-reduced-motion: reduce` is active, hero title animation is disabled and content remains immediately visible.

Verification evidence:
- Design regression script: `node scripts/test-diary-green-planet-design.cjs` passed 17/17 checks.
- Build: `npm run build:local` completed successfully.
- Browser capture: Chrome at 1440 x 1024.
- First viewport metrics: hero height 1024, viewport height 1024, content top 1024, header background `rgba(0, 0, 0, 0)`.
- Scrolled metrics: content top 0, header background `rgba(7, 18, 13, 0.72)`, header class `site-header header-scrolled`.
- Animation metrics: hero backdrop transform changed from `matrix(1.08, 0, 0, 1.08, 0, 0)` to `matrix(1.08, 0, 0, 1.08, 0, 21.919)` during scroll; diary content class changed to `content-visible`; reduced motion reported `titleAnimation: none`.
- Side bloom metrics: bloom progress changed from `0.000` when scrolling back up to `1.000` near the diary section; side layer opacity changed from `0` to `0.56`.

Notes:
- The local backend was not running during capture, so Chrome reports one network resource failure for the diary API request. The public homepage now uses a silent request path: no visible error toast appears and no mounted-hook page error is thrown.
