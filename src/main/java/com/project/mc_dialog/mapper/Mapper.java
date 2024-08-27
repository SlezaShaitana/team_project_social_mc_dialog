package com.project.mc_dialog.mapper;

import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.MessageShortDto;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    @Mapping(target = "lastMessage", source = "messages")
    DialogDto dialogToDto(Dialog dialog);

    default List<DialogDto> dialogListToDialogDtoList(List<Dialog> dialogs) {
        List<DialogDto> dialogDtoList = new ArrayList<>();
        dialogs.forEach(dialog -> dialogDtoList.add(dialogToDto(dialog)));
        return dialogDtoList;
    }

    MessageShortDto messageToShortDto(Message message);

    @Mapping(source = "dialog.id", target = "dialogId")
    MessageDto messageToDto(Message message);

    default List<MessageShortDto> messageListToMessageShortDtoList(List<Message> messages) {
        List<MessageShortDto> shortMessageList = new ArrayList<>();
        messages.forEach(message -> shortMessageList.add(messageToShortDto(message)));
        return  shortMessageList;
    }

    default List<MessageDto> map(List<Message> messages) {
        return !messages.isEmpty() ? List.of(messageToDto(messages.get(messages.size() - 1))) : List.of();
    }

}
