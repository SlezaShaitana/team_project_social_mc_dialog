package com.project.mc_dialog.controller;

import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.service.DialogService;
import com.project.mc_dialog.service.MessageService;
import com.project.mc_dialog.testContainer.PostgresContainer;
import com.project.mc_dialog.web.controller.DialogController;
import com.project.mc_dialog.web.dto.Pageable;
import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for DialogController")
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false)
public class DialogControllerTests extends PostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Mapper mapper;

    @Mock
    private MessageService messageService;

    @Mock
    private DialogService dialogService;

    @InjectMocks
    private DialogController dialogController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(dialogController)
                .build();
    }

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test updateMessageStatus")
    public void testUpdateMessageStatus() throws Exception {
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/dialogs/{dialogId}", dialogId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk());
        verify(messageService, times(1)).updateMessageStatus(dialogId);
    }

    @Test
    @DisplayName("Test getDialogs")
    public void getDialogs() throws Exception {
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

        List<Dialog> content = List.of(
                Dialog.builder().id(UUID.randomUUID()).build(),
                Dialog.builder().id(UUID.randomUUID()).build()
        );
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

        String response = objectMapper.writeValueAsString(pageDialogDto);

        when(dialogService.getDialogs(pageableDto)).thenReturn(pageDialogDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dialogs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
        verify(dialogService, times(1)).getDialogs(pageableDto);
    }

    @Test
    @DisplayName("Test getUnreadMessages")
    public void testGetUnreadMessages() throws Exception {
        UnreadCountDto countDto = new UnreadCountDto();
        countDto.setCount(0);

        String response = objectMapper.writeValueAsString(countDto);

        when(dialogService.getUnreadCount()).thenReturn(countDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dialogs/unread")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(response));
        verify(dialogService, times(1)).getUnreadCount();
    }

    @Test
    @DisplayName("Test getDialog")
    public void getDialog() throws Exception {
        UUID recipientId = UUID.randomUUID();
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa3");

        DialogDto dialogDto = DialogDto.builder().id(dialogId).build();

        when(dialogService.getDialog(recipientId)).thenReturn(dialogDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dialogs/recipientId/{id}", recipientId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(dialogDto)));
        verify(dialogService, times(1)).getDialog(recipientId);
    }

    @Test
    @DisplayName("Test getMessages")
    public void getMessages() throws Exception {
        UUID recipientId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
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

        List<Message> content = List.of(
                Message.builder().id(UUID.randomUUID()).build(),
                Message.builder().id(UUID.randomUUID()).build()
        );
        PageMessageShortDto pageMessageShortDto = PageMessageShortDto.builder()
                .totalPages(1)
                .totalElements(2L)
                .sort(sortDto)
                .numberOfElements(2)
                .pageable(pageableObject)
                .first(true)
                .last(true)
                .size(2)
                .content(mapper.messageListToMessageShortDtoList(content))
                .number(0)
                .empty(false)
                .build();

        when(messageService.getMessages(recipientId, pageableDto)).thenReturn(pageMessageShortDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/dialogs/messages")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .param("recipientId", recipientId.toString())
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(pageMessageShortDto)));
        verify(messageService, times(1)).getMessages(recipientId, pageableDto);
    }

}
