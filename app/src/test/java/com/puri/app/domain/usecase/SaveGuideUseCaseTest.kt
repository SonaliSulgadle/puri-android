package com.puri.app.domain.usecase

import com.puri.app.core.common.Resource
import com.puri.app.fake.FakeSavedGuidesRepository
import com.puri.app.util.TestFixtures.savedGuideTrash
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SaveGuideUseCase")
class SaveGuideUseCaseTest {

    private lateinit var useCase: SaveGuideUseCase
    private lateinit var fakeSavedGuidesRepo: FakeSavedGuidesRepository

    @BeforeEach
    fun setUp() {
        fakeSavedGuidesRepo = FakeSavedGuidesRepository()
        useCase = SaveGuideUseCase(fakeSavedGuidesRepo)
    }

    @Nested
    @DisplayName("given valid guide")
    inner class ValidGuide {

        @Test
        @DisplayName("returns Success after saving")
        fun returnsSuccess() = runTest {
            val result = useCase(savedGuideTrash)
            assertThat(result).isInstanceOf(Resource.Success::class.java)
        }

        @Test
        @DisplayName("guide appears in saved guides after saving")
        fun guideAppearsInList() = runTest {
            useCase(savedGuideTrash)

            val guides = fakeSavedGuidesRepo.getSavedGuides().first()
            assertThat(guides).hasSize(1)
            assertThat(guides.first().title).isEqualTo("Trash Sorting")
        }

        @Test
        @DisplayName("multiple guides can be saved")
        fun multipleGuidesCanBeSaved() = runTest {
            useCase(savedGuideTrash)
            useCase(savedGuideTrash.copy(id = 2L, title = "Washing Machine"))

            val guides = fakeSavedGuidesRepo.getSavedGuides().first()
            assertThat(guides).hasSize(2)
        }
    }
}