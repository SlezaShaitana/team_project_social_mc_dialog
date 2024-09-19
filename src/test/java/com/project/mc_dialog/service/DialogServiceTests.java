package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.testContainer.PostgresContainer;
import com.project.mc_dialog.web.dto.Pageable;
import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for DialogService")
public class DialogServiceTests extends PostgresContainer {

    @Mock
    private Mapper mapper;

    @Mock
    private DialogRepository dialogRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private DialogService dialogService;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test createDialog")
    public void testCreateDialog() {
        UUID conversationPartner1 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID conversationPartner2 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa2");

        DialogDto dialogDto = DialogDto.builder()
                .unreadCount(0)
                .isDeleted(false)
                .conversationPartner1(conversationPartner1)
                .conversationPartner2(conversationPartner2)
                .build();

        Dialog dialog = Dialog.builder()
                .conversationPartner1(conversationPartner1)
                .conversationPartner2(conversationPartner2)
                .build();

        when(dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                conversationPartner1,
                conversationPartner2,
                conversationPartner2,
                conversationPartner1))
                .thenReturn(Optional.empty());
        when(dialogRepository.save(any(Dialog.class))).thenReturn(dialog);
        when(mapper.dialogToDto(dialog)).thenReturn(dialogDto);

        DialogDto result = dialogService.createDialog(dialogDto);

        assertEquals(dialogDto, result);
        verify(dialogRepository, times(1)).save(any(Dialog.class));
        verify(mapper, times(1)).dialogToDto(dialog);

    }

    @Test
    @DisplayName("Test getDialogs")
    public void testGetDialogs() {
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        Pageable pageableDto = Pageable.builder()
                .page(0)
                .size(5)
                .sort(null)
                .build();
        Sort sortDto = Sort.builder()
                .sorted(false)
                .unsorted(true)
                .empty(true)
                .build();
        PageableObject pageableObject = PageableObject.builder()
                .sort(sortDto)
                .unpaged(false)
                .paged(true)
                .pageSize(5)
                .pageNumber(0)
                .offset(0)
                .build();
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 5, org.springframework.data.domain.Sort.unsorted());

        List<Dialog> content = List.of(
                Dialog.builder().id(UUID.randomUUID()).build(),
                Dialog.builder().id(UUID.randomUUID()).build()
        );
        Page<Dialog> dialogPage = new PageImpl<>(content);
        PageDialogDto pageDialogDto = PageDialogDto.builder()
                .totalPages(1)
                .totalElements(2L)
                .sort(sortDto)
                .numberOfElements(2)
                .pageable(pageableObject)
                .first(true)
                .last(true)
                .size(2)
                .content(mapper.dialogListToDialogDtoList(content))
                .number(0)
                .empty(false)
                .build();

        when(authenticationService.getCurrentUserId()).thenReturn(ownerId);
        when(dialogRepository.findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId, pageable))
                .thenReturn(dialogPage);

        PageDialogDto result = dialogService.getDialogs(pageableDto);

        assertEquals(pageDialogDto, result);
        verify(authenticationService, times(1)).getCurrentUserId();
        verify(dialogRepository, times(1)).findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId, pageable);

    }

    @Test
    @DisplayName("Test getUnreadCount")
    public void testGetUnreadCount() {
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        List<Dialog> content = List.of(
                Dialog.builder().id(UUID.randomUUID()).unreadCount(0).build(),
                Dialog.builder().id(UUID.randomUUID()).unreadCount(0).build()
        );

        UnreadCountDto unreadCountDto = new UnreadCountDto();
        unreadCountDto.setCount(0);

        when(authenticationService.getCurrentUserId()).thenReturn(ownerId);
        when(dialogRepository.findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId))
                .thenReturn(content);

        UnreadCountDto result = dialogService.getUnreadCount();

        assertEquals(unreadCountDto, result);
        verify(authenticationService, times(1)).getCurrentUserId();
        verify(dialogRepository, times(1)).findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId);
    }

    @Test
    @DisplayName("Test getDialog")
    public void testGetDialog() {
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID recipientId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa2");
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa3");
        Dialog existingDialog = Dialog.builder().id(dialogId).build();
        DialogDto dialogDto = DialogDto.builder().id(dialogId).build();

        when(authenticationService.getCurrentUserId()).thenReturn(ownerId);
        when(dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                ownerId,
                recipientId,
                recipientId,
                ownerId))
                .thenReturn(Optional.of(existingDialog));
        when(mapper.dialogToDto(existingDialog)).thenReturn(dialogDto);

        DialogDto result = dialogService.getDialog(recipientId);

        assertEquals(dialogDto, result);
        verify(authenticationService, times(1)).getCurrentUserId();
        verify(dialogRepository, times(1)).findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                ownerId,
                recipientId,
                recipientId,
                ownerId
        );
        verify(mapper, times(1)).dialogToDto(existingDialog);

    }

    @Test
    @DisplayName("Test getExistingDialog when dialog exists")
    public void testGetExistingDialogWhenDialogExists() {
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");

        Dialog existingDialog = Dialog.builder()
                .id(dialogId)
                .unreadCount(0)
                .isDeleted(false)
                .conversationPartner1(UUID.randomUUID())
                .conversationPartner2(UUID.randomUUID())
                .messages(new ArrayList<>())
                .build();

        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(existingDialog));

        Dialog result = dialogService.getExistingDialog(dialogId);

        assertEquals(existingDialog, result);
        verify(dialogRepository, times(1)).findById(dialogId);
    }

    @Test
    @DisplayName("Test getExistingDialog when dialog does not exist")
    void testGetExistingDialogWhenDialogDoesNotExist() {
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");

        when(dialogRepository.findById(dialogId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DialogNotFoundException.class,
                () -> dialogService.getExistingDialog(dialogId));

        assertEquals("Dialog with id " + dialogId + " not found", exception.getMessage());
        verify(dialogRepository, times(1)).findById(dialogId);
    }

    @Test
    @DisplayName("Test getExistingDialog when dialog exists with users ids")
    public void testGetExistingDialogWhenDialogExistsWithUserIds() {
        UUID conversationPartner1 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID conversationPartner2 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa2");

        Dialog existingDialog = Dialog.builder()
                .id(UUID.randomUUID())
                .unreadCount(0)
                .isDeleted(false)
                .conversationPartner1(UUID.randomUUID())
                .conversationPartner2(UUID.randomUUID())
                .messages(new ArrayList<>())
                .build();

        when(dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                conversationPartner1,
                conversationPartner2,
                conversationPartner2,
                conversationPartner1))
                .thenReturn(Optional.of(existingDialog));

        Dialog result = dialogService.getExistingDialog(conversationPartner1, conversationPartner2);

        assertEquals(existingDialog, result);
        verify(dialogRepository, times(1)).findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                conversationPartner1,
                conversationPartner2,
                conversationPartner2,
                conversationPartner1
        );
    }

    @Test
    @DisplayName("Test getExistingDialog when dialog exists with users ids does not exist")
    public void testGetExistingDialogWhenDialogExistsWithUserIdsDoesNotExist() {
        UUID conversationPartner1 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID conversationPartner2 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa2");

        when(dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                conversationPartner1,
                conversationPartner2,
                conversationPartner2,
                conversationPartner1))
                .thenReturn(Optional.empty());

        Dialog result = dialogService.getExistingDialog(conversationPartner1, conversationPartner2);

        assertNull(result);
        verify(dialogRepository, times(1)).findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                conversationPartner1,
                conversationPartner2,
                conversationPartner2,
                conversationPartner1
        );
    }

}
