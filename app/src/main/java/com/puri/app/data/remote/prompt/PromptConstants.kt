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
CONFIDENCE: [HIGH or LOW]
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
CONFIDENCE: [HIGH or LOW]
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
}