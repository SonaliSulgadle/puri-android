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
Use EXACTLY this format — no text before or after:

WHAT: [one phrase — what this specific thing is]
DESCRIPTION: [one directly useful sentence, or NONE]
VISIBLE TEXT:
[Korean] → [English] — [practical meaning]
[every Korean label/sign/button — skip section entirely if none or English-only]
STEPS:
1. [action] | [clarification]
[only steps user needs to DO — skip section if no action needed]
WARNING: [one sentence only if real fine/health/safety risk — otherwise NONE]
TIP: [one Korea-specific thing a new arrival wouldn't know — otherwise NONE]
RECOMMENDED ACTION: [the single most important thing right now, or NONE]
CONFIDENCE: [HIGH or LOW]
CATEGORY: [TRASH / APPLIANCE / TRANSPORT / FOOD / MEDICAL / GENERAL]
""".trimIndent()
}