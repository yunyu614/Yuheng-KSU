# Yuheng Five Beasts — Open Artwork Source Candidates

This file records candidate source artworks for the Five Beasts visual pack. These are **source references, not yet bundled app assets**. Before adding any image binary to `manager/app/src/main/res/drawable-nodpi/`, download the original-resolution file from the institution's own page, verify the image and rights metadata, create optimized derivatives, and preserve this record.

## Preferred source set

| Theme | Candidate artwork | Source and rights | Intended treatment |
|---|---|---|---|
| Qinglong / 青龙 | *Dragon*, Fachang Muqi, c. 1250–1279, Southern Song | Cleveland Museum of Art, object 1958.427.1. Open Access / CC0; the museum explicitly permits copying, modification and distribution. https://www.clevelandart.org/art/1958.427.1 | Isolate the dragon and mist/wave passage; jade-green duotone for hero, muted crop for background. |
| Baihu / 白虎 | *Tiger*, Fachang Muqi, c. 1250–1279, Southern Song | Cleveland Museum of Art, object 1958.427.2. Open Access / CC0; the museum explicitly permits copying, modification and distribution. https://www.clevelandart.org/art/1958.427.2 | Pair with the Qinglong scroll for a coherent artist/period visual language; silver/graphite treatment. |
| Xuanwu / 玄武 | *Tortoise and Snake of the North (Xuanwu)*, early 6th century | Princeton University Art Museum, object y1956-17. The object page offers downloadable public-domain images. https://artmuseum.princeton.edu/art/collections/objects/28015 | Use as the unmistakable turtle-and-snake emblem; build a separate water/mountain background rather than stretching the sculpture photo. |
| Zhuque / 朱雀 | *A Hundred Birds Worship the Phoenixes*, Qing dynasty | Smithsonian National Museum of Asian Art, object F1909.218. CC0 / public domain. https://asia.si.edu/explore-art-culture/collections/search/edanmdm%3Afsg_F1909.218/ | Crop a clear phoenix detail where resolution permits; cinnabar/red-gold color grade and feather motif. |
| Qilin / 麒麟 | *Rank Badge with Qilin*, Qing dynasty | The Metropolitan Museum of Art, object 30.75.922. Public Domain; The Met Open Access images are available for unrestricted commercial and noncommercial use. https://www.metmuseum.org/art/collection/search/68898 | Prefer the embroidered qilin motif as a decorative emblem; purple-jade and antique-gold treatment. |

## Licensing and implementation rules

- Cleveland Museum of Art CC0 images can be modified and redistributed without attribution requirements; retaining attribution is still recommended.
- Smithsonian object F1909.218 is marked CC0 / public domain.
- Princeton's object page identifies the downloadable image as a public-domain work; retain the museum caption and source URL in this file.
- The Met object 30.75.922 is marked Public Domain and covered by the Met Open Access policy.
- Re-check each source page and the actual downloaded file before release. Do not assume every image on a museum site is CC0.
- Do not hotlink museum image URLs at runtime. Bundle optimized local assets so the app works offline and does not depend on third-party servers.
- Do not use a single historic painting as a full-screen wallpaper without composition work. Build a consistent pack: transparent/cropped creature emblem, separate low-contrast scene background, and small ornamental texture for each preset.
- Suggested outputs: 1) hero illustration PNG/WebP with transparency, 2) 9:16 background WebP, 3) small repeating ornament PNG/WebP. Keep originals and attribution metadata in a separate source archive.

## Asset status

- [ ] Original-resolution source files downloaded and visually inspected
- [ ] Rights metadata checked against each exact file
- [ ] Five hero illustrations cropped and color-treated
- [ ] Five matching backgrounds prepared
- [ ] Optimized local resources added to the Android project
- [ ] Attribution/source records reviewed for release
