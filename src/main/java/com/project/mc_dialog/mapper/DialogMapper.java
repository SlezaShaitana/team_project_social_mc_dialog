package com.project.mc_dialog.mapper;

import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DialogMapper {

    @Mapping(target = "lastMessage", source = "messages")
    DialogDto dialogToDto(Dialog dialog);

    @Mapping(source = "dialog.id", target = "dialogId")
    MessageDto messageToDto(Message message);

    default List<MessageDto> map(List<Message> messages) {
        return messages != null ? List.of(messageToDto(messages.get(messages.size() - 1))) : List.of();
    }

}
