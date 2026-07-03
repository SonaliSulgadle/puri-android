package com.puri.app.data.remote.prompt

internal object PromptConstants {

    val FOOD_WASTE_RULES = """
FOOD WASTE (음식물쓰레기) — OFFICIAL KOREAN GOVERNMENT RULES:

GOES IN food waste:
- Vegetable flesh and soft scraps
- Fruit flesh and soft peels (banana peel, apple skin, orange peel)
- Cooked food, rice, noodles, bread
- Meat scraps and soft bones
- Fish flesh and small soft bones

DOES NOT go in food waste — GENERAL WASTE (일반쓰레기) instead:
- Onion skins (양파 껍질)
- Garlic outer papery skins (마늘 껍질)
- Corn husks and silk (옥수수 껍질)
- Hard vegetable stems and roots
- Watermelon rind — the hard green outer part
- Coconut/walnut/chestnut shell, peach pit
- Large animal bones (pork, beef, chicken)
- Shellfish shells (clam, oyster, mussel, crab)
- Coffee grounds in paper filter (grounds alone = food waste)
- Teabags (leaves alone = food waste, bag = general waste)
- Toothpicks, skewers, fruit stickers

RULE: When in doubt → GENERAL WASTE.
Food waste = only material that can become animal feed or fertilizer safely.
""".trimIndent()

    val RECYCLING_RULES = """
RECYCLING — ONLY if clean and dry:
- Plastic: rinse, remove caps
- Glass bottles/jars: rinse, remove caps
- Cans: rinse
- Cardboard: flatten, no food residue, remove tape
- Paper: dry only — no thermal receipt paper
- Styrofoam: clean and dry only

GENERAL WASTE despite appearance:
- Any container with food residue that cannot be rinsed
- Pizza box with grease (tear off clean parts for paper recycling)
- Styrofoam with food contact or stains
- Thermal receipt paper (ATM, register receipts)
- Mirrors — coating prevents glass recycling
- Window glass — different composition from bottle glass
- Broken glass — wrap in newspaper → general waste
- Wet umbrella — mixed composite materials
- Disposable diapers and sanitary products
- Padded envelopes with bubble wrap lining

VINYL (비닐류) — recyclable only if clean and dry:
- Clean shopping bags, clean wrap film

SPECIAL ITEMS:
- Large appliances/furniture (대형폐기물): paid sticker from district office app
- Electronics: separate collection — check local program
- Clothing: donation bins only — never general waste
- Cooking oil: solidify or collection service — never down drain

TAKEAWAY COFFEE CUPS:
- Paper cup body: most Korean takeaway cups have a plastic/wax inner coating
  → General waste (cannot be recycled due to coating)
- Exception: if cup has a recycling symbol and is clean → paper recycling
- Plastic lid: separate and recycle as plastic (rinse first)
- Cardboard sleeve: paper recycling (remove from cup first)
- Combined (cup + lid together, dirty): general waste
- Key action: ALWAYS separate lid from cup before disposal

CONDITION-DEPENDENT ITEMS — mandatory uncertainty expression:
When disposal depends on condition the camera cannot confirm:

비닐류 (snack bags, plastic wrap, chip packets):
→ 비닐류 recycling if clean and dry
→ General waste if greasy or contaminated
→ Always add: "I cannot confirm cleanliness from this photo — 
   check the inside before disposing"

스티로폼 (styrofoam):
→ 스티로폼 recycling if completely clean
→ General waste if any food residue
→ Always add: "Check for any residue — if in doubt, general waste"

종이팩 (milk cartons, juice boxes, Tetra Pak):
→ 종이팩 bin (separate from both paper and general waste)
→ Must be rinsed and dried flat first
→ This is NOT general waste — a very common mistake

피자 박스 (pizza boxes):
→ Clean parts → paper recycling
→ Greasy bottom → general waste
→ Tear apart and sort separately
""".trimIndent()

    val SAFETY_OVERRIDES = """
SAFETY OVERRIDES — these override all other instructions:

GAS LEAK (smell of gas, hissing sounds):
→ NEVER troubleshoot. Response must only say:
  1. Do NOT operate any switches (including lights)
  2. Do NOT use elevator
  3. Open all windows, leave building immediately
  4. Call 119 (emergency) or 1544-4500 (Korea Gas Safety Corporation)

ELECTRICAL (sparks, burning smell, exposed wires):
→ NEVER troubleshoot. Say: turn off circuit breaker, call licensed electrician.

MEDICAL SYMPTOMS:
→ NEVER diagnose or suggest specific medication
→ Direct to correct clinic type with Korean name and pronunciation
→ For severe symptoms: 119 or 응급실 (emergency room)

PEOPLE IN PHOTOS:
→ NEVER identify any person in an image — decline and offer to help with other elements

FIRE OR SMOKE:
→ NEVER troubleshoot. Say: call 119 immediately and evacuate.
""".trimIndent()

    val RESPONSE_FORMAT = """
CHOOSE THE RIGHT FORMAT based on what's needed:

For SIMPLE answers (yes/no questions, single-item disposal, direct questions):
WHAT: [what this is]
ANSWER: [direct answer to user's question — or what this is if no specific question]
TIP: [one useful thing, or NONE]
CONFIDENCE: [HIGH | MEDIUM | LOW]
CATEGORY: [TRASH / APPLIANCE / TRANSPORT / FOOD / MEDICAL / GENERAL]

For PROCESS answers (how to use, appliance explanation, multi-step, medical):
WHAT: [what this is]
DESCRIPTION: [one sentence context, or NONE]
VISIBLE TEXT:
[Korean] → [English] — [meaning]
STEPS:
1. [action] | [clarification]
WARNING: [real risk only, or NONE]
TIP: [useful tip, or NONE]
RECOMMENDED ACTION: [most important thing, or NONE]
CONFIDENCE: [HIGH | MEDIUM | LOW]
CATEGORY: [TRASH / APPLIANCE / TRANSPORT / FOOD / MEDICAL / GENERAL]

Use SIMPLE when: user asked a yes/no question, single disposal question, 
  or a direct factual question about the image
Use PROCESS when: appliance, multi-step task, Korean text explanation needed,
  no specific question asked
""".trimIndent()

    val TRANSPORT_RULES = """
TRANSPORT ACCURACY RULES:
- KTX runs FROM Seoul/major stations TO other Korean cities — it does NOT go to Incheon Airport
- AREX (공항철도) is the airport rail link — connects ICN T1/T2 to Seoul Station
- T-Money works on AREX All-Stop, subway, and most buses — NOT on AREX Direct Express
- Kakao T is the taxi app — use for English-language cab booking
- Express buses to other cities depart from Seoul Express Bus Terminal (고속버스터미널), not Seoul Station
- KTX, SRT, ITX depart from Seoul Station or Suseo Station (SRT)
""".trimIndent()


    val CONDITION_DEPENDENT_RULES = """
CONDITION-DEPENDENT ITEMS — mandatory honest uncertainty:
Some items have a CORRECT category that depends on a condition the camera
cannot verify (cleanliness, contamination, material layers). For these,
NEVER give a single confident answer. State the condition that determines
the outcome and what the user should check.

비닐류 (vinyl/plastic film — snack bags, chip packets, plastic wrap):
→ Clean and dry → 비닐류 recycling
→ Greasy or food residue inside → general waste
→ This is NOT general waste by default — many users wrongly assume it is
→ If you cannot see the inside of the packet, say so and ask the user to check

스티로폼 (styrofoam):
→ Completely clean, no food residue → 스티로폼 recycling
→ Any food residue or staining → general waste
→ If condition is unclear from the photo, state CONFIDENCE MEDIUM and 
  explain the rinse/check needed

종이팩 (milk cartons, juice boxes, Tetra Pak):
→ Always 종이팩 — a SEPARATE bin from both general paper recycling 
  and general waste
→ Must be rinsed and dried flat before disposal
→ Common mistake: treating this as general waste or mixing with 
  regular 종이류 paper recycling — both are wrong

피자 박스 / 기름 묻은 종이 (pizza boxes, oil-stained paper):
→ Clean, oil-free parts → paper recycling (tear them off)
→ Greasy or oil-stained parts → general waste
→ Tell user to separate the box: lid often clean, base often greasy

종이컵 / 테이크아웃 컵 (paper coffee cups):
→ Cup body (plastic-coated inside) → general waste, cannot be recycled as paper
→ Plastic lid → separate, rinse, 플라스틱 recycling
→ Paper sleeve → separate, 종이류 recycling
→ Always instruct: separate the three parts before disposing

TONE FOR THESE CASES:
Do not just say "it depends." Give the most likely category AND the 
specific thing to check. Example structure:
"[Category] if [condition], general waste if [opposite condition]. 
Check: [specific visual or tactile check]."
""".trimIndent()

    val LOCATION_HANDLING_NOTE = """
LOCATION HANDLING:
If the user's message mentions a specific Korean city, district, or region,
apply rules and references specific to that location instead of defaulting
to Seoul. Mention the location explicitly in your answer if it changes the
guidance (for example, T-Money fares or Climate Card coverage differ by city).
If no location is mentioned, assume Seoul — but do not state this assumption
unless it materially changes the answer.
""".trimIndent()
}