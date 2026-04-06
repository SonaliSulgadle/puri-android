package com.puri.app.util

import com.puri.app.domain.model.Category
import com.puri.app.domain.model.ConfidenceLevel
import com.puri.app.domain.model.HistoryItem
import com.puri.app.domain.model.SavedGuide
import com.puri.app.domain.model.SolveResult
import com.puri.app.domain.model.SolveStep

object TestFixtures {

    val solveSteps = listOf(
        SolveStep(order = 1, title = "Rinse", description = "Remove all food residue"),
        SolveStep(order = 2, title = "Dry", description = "Ensure no moisture remains"),
        SolveStep(
            order = 3,
            title = "Recyclable Bin",
            description = "Place in the Plastic container"
        )
    )

    val trashSolveResult = SolveResult(
        id = 1L,
        whatThisIs = "LDPE Plastic Container",
        description = "Commonly used for food storage and squeeze bottles",
        steps = solveSteps,
        warning = "Do not mix with food waste",
        koreaTip = "Look for the recycling triangle with number 4",
        category = Category.TRASH,
        confidenceLevel = ConfidenceLevel.HIGH,
        imageUri = "content://media/external/images/1",
        inputQuery = null
    )

    val applianceSolveResult = SolveResult(
        id = 2L,
        whatThisIs = "Gas Stove",
        description = "Standard apartment gas range",
        steps = listOf(
            SolveStep(order = 1, title = "Check valve", description = "Ensure gas valve is open"),
            SolveStep(order = 2, title = "Press ignition", description = "Hold the ignition button")
        ),
        warning = "Turn off gas valve when not in use",
        koreaTip = "Korean gas stoves use LPG — different from Western natural gas",
        category = Category.APPLIANCE,
        confidenceLevel = ConfidenceLevel.HIGH,
        imageUri = null,
        inputQuery = "How do I use this gas stove?"
    )

    val uncertainSolveResult = SolveResult(
        id = 3L,
        whatThisIs = "",
        description = "",
        steps = emptyList(),
        warning = null,
        koreaTip = null,
        category = Category.GENERAL,
        confidenceLevel = ConfidenceLevel.LOW,
        imageUri = "content://media/external/images/2",
        inputQuery = null
    )

    val textSolveResult = SolveResult(
        id = 4L,
        whatThisIs = "Subway T-Money Card Top-up",
        description = "How to recharge your transit card",
        steps = listOf(
            SolveStep(
                order = 1,
                title = "Find machine",
                description = "Look for blue T-Money machines"
            ),
            SolveStep(order = 2, title = "Insert card", description = "Place card on the reader"),
            SolveStep(order = 3, title = "Select amount", description = "Choose recharge amount")
        ),
        warning = null,
        koreaTip = "T-Money works on all Seoul Metro lines and most buses",
        category = Category.TRANSPORT,
        confidenceLevel = ConfidenceLevel.HIGH,
        imageUri = null,
        inputQuery = "How do I top up my subway card?"
    )

    val historyItemToday = HistoryItem(
        id = 1L,
        solveResult = trashSolveResult,
        timestamp = System.currentTimeMillis(),
        isSaved = false
    )

    val historyItemYesterday = HistoryItem(
        id = 2L,
        solveResult = applianceSolveResult,
        timestamp = System.currentTimeMillis() - 86_400_000L,
        isSaved = true
    )

    val savedGuideTrash = SavedGuide(
        id = 1L,
        title = "Trash Sorting",
        description = "Master the art of waste management",
        category = Category.TRASH,
        solveResult = null,
        isPreBundled = true,
        isFeatured = false,
        imageUri = null
    )

    val savedGuideGasStove = SavedGuide(
        id = 2L,
        title = "Gas stove",
        description = "Safety protocols and ignition guides",
        category = Category.APPLIANCE,
        solveResult = applianceSolveResult,
        isPreBundled = false,
        isFeatured = true,
        imageUri = null
    )
}