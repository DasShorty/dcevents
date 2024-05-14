package de.dasshorty.dcevents.rating.image.rating;

import de.dasshorty.dcevents.api.buttons.Button;
import de.dasshorty.dcevents.rating.image.submit.SubmitDto;
import de.dasshorty.dcevents.rating.image.submit.SubmitRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Optional;

public class RatingButton implements Button {

    private final SubmitRepository submitRepo;

    public RatingButton(SubmitRepository submitRepo) {
        this.submitRepo = submitRepo;
    }

    @Override
    public String id() {
        return "image-rating-button";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();
        String[] split = event.getButton().getLabel().split(" ");

        if (split.length != 2) {
            event.reply("Something went wrong with the button label!").setEphemeral(true).queue();
            return;
        }

        Guild guild = event.getGuild();

        Message message = event.getMessage();

        event.deferReply(true).queue();

        Optional<SubmitDto> optional = this.submitRepo.findByMessageId(message.getId());

        if (optional.isEmpty()) {
            event.getHook().editOriginal("Rating can't be found with message id: " + message.getId()).queue();
            return;
        }

        SubmitDto dto = optional.get();

        if (dto.getMembersThatVoted().contains(member.getId())) {
            event.getHook().editOriginal("You have already given points to this image!").queue();
            return;
        }

        try {
            int count = Integer.parseInt(split[0]);

            dto.setRating(dto.getRating() + count);
            dto.getMembersThatVoted().add(member.getId());

            this.submitRepo.save(dto);

            event.getHook().editOriginal("You gave " + count + " points").queue();

        } catch (NumberFormatException e) {
            event.getHook().editOriginal("Something went wrong with parsing the data to the backend!").queue();
        }

    }
}
