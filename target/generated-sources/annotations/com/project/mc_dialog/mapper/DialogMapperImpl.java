package com.project.mc_dialog.mapper;

import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.MessageShortDto;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-27T12:07:39+0400",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class DialogMapperImpl implements DialogMapper {

    @Override
    public DialogDto dialogToDto(Dialog dialog) {
        if ( dialog == null ) {
            return null;
        }

        DialogDto dialogDto = new DialogDto();

        dialogDto.setLastMessage( map( dialog.getMessages() ) );
        dialogDto.setId( dialog.getId() );
        dialogDto.setDeleted( dialog.isDeleted() );
        dialogDto.setUnreadCount( dialog.getUnreadCount() );
        dialogDto.setConversationPartner1( dialog.getConversationPartner1() );
        dialogDto.setConversationPartner2( dialog.getConversationPartner2() );

        return dialogDto;
    }

    @Override
    public MessageShortDto messageToShortDto(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageShortDto messageShortDto = new MessageShortDto();

        messageShortDto.setId( message.getId() );
        messageShortDto.setDeleted( message.isDeleted() );
        messageShortDto.setTime( message.getTime() );
        messageShortDto.setConversationPartner1( message.getConversationPartner1() );
        messageShortDto.setConversationPartner2( message.getConversationPartner2() );
        messageShortDto.setMessageText( message.getMessageText() );

        return messageShortDto;
    }

    @Override
    public MessageDto messageToDto(Message message) {
        if ( message == null ) {
            return null;
        }

        MessageDto messageDto = new MessageDto();

        messageDto.setDialogId( messageDialogId( message ) );
        messageDto.setId( message.getId() );
        messageDto.setDeleted( message.isDeleted() );
        messageDto.setTime( message.getTime() );
        messageDto.setConversationPartner1( message.getConversationPartner1() );
        messageDto.setConversationPartner2( message.getConversationPartner2() );
        messageDto.setMessageText( message.getMessageText() );
        messageDto.setReadStatus( message.getReadStatus() );

        return messageDto;
    }

    private UUID messageDialogId(Message message) {
        if ( message == null ) {
            return null;
        }
        Dialog dialog = message.getDialog();
        if ( dialog == null ) {
            return null;
        }
        UUID id = dialog.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
