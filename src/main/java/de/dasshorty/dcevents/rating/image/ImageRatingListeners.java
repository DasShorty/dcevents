package de.dasshorty.dcevents.rating.image;

import de.dasshorty.dcevents.rating.image.submit.SubmitDto;
import de.dasshorty.dcevents.rating.image.submit.SubmitRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bson.types.Binary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ImageRatingListeners extends ListenerAdapter {

    private final ImageRepository imageRepository;
    private final SubmitRepository submitRepository;

    public ImageRatingListeners(ImageRepository imageRepository, SubmitRepository submitRepository) {
        this.imageRepository = imageRepository;
        this.submitRepository = submitRepository;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (member == null) {
            return;
        }

        if (member.getUser().isBot()) {
            return;
        }

        if (!event.getChannelType().isGuild()) {
            return;
        }

        if (!this.imageRepository.existsByServerId(guild.getId())) {
            return;
        }

        MessageChannelUnion channel = event.getChannel();

        Optional<ImageRatingDto> optional = this.imageRepository.getBySubmitChannelId(channel.getId());

        if (optional.isEmpty()) {
            return;
        }

        ImageRatingDto imageRatingDto = optional.get();

        if (imageRatingDto.getState() != ImageRatingState.SUBMIT) {
            return;
        }


        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.isEmpty()) {

            this.removeMessage(event.getMessage());
            return;
        }

        for (Message.Attachment attachment : attachments) {

            String url = attachment.getUrl();
            try {

                byte[] bytes = this.downloadImage(url);
                SubmitDto dto = new SubmitDto();
                dto.setId(member.getId());
                dto.setImage(new Binary(bytes));
                dto.generateImageId();
                dto.setRatingId(imageRatingDto.getId());

                this.submitRepository.save(dto);

                this.removeMessage(event.getMessage());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private byte[] downloadImage(String url) throws IOException {
        InputStream inputStream = new URL(url).openStream();
        return inputStream.readAllBytes();
    }

    private void removeMessage(Message message) {

        if (message == null) {
            return;
        }

        message.delete().queueAfter(5, TimeUnit.SECONDS);
    }
}
