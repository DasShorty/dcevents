package de.dasshorty.dcevents.rating.image;

import de.dasshorty.dcevents.api.commands.slash.SlashCommand;
import de.dasshorty.dcevents.rating.image.submit.SubmitDto;
import de.dasshorty.dcevents.rating.image.submit.SubmitRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ImageRatingSetupCommand implements SlashCommand {

    private final ImageRepository imageRepo;
    private final SubmitRepository submitRepo;

    public ImageRatingSetupCommand(ImageRepository imageRepo, SubmitRepository submitRepo) {
        this.imageRepo = imageRepo;
        this.submitRepo = submitRepo;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("image-rating", "Setup Image Rating")
                .addSubcommands(
                        new SubcommandData("create", "Create an Image Rating Competition")
                                .addOption(OptionType.CHANNEL, "input", "Where can users send images to the competition", true)
                                .addOption(OptionType.CHANNEL, "vote", "Where should users rate the images", true),
                        new SubcommandData("start", "Start an image rating")
                                .addOption(OptionType.STRING, "id", "Image Rating id", true)
                                .addOption(OptionType.ROLE, "mention", "A role that is been pinged by the bot", true),
                        new SubcommandData("voting", "Changes the state of the image rating to vote")
                                .addOption(OptionType.STRING, "id", "Image Rating id", true),
                        new SubcommandData("stop", "Stop an image rating")
                                .addOption(OptionType.STRING, "id", "Image Rating id", true)
                                .addOption(OptionType.CHANNEL, "result", "Where should the result sent into?", true)
                                .addOption(OptionType.ROLE, "ping", "Which role should be pinged?", true)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (guild == null) {
            event.reply("Something went wrong but the bot couldn't get the guild :(").setEphemeral(true).queue();
            return;
        }

        switch (event.getSubcommandName().toLowerCase()) {
            case "create" -> {

                event.deferReply(true).queue();
                GuildChannelUnion inputChannel = event.getOption("input", OptionMapping::getAsChannel);
                GuildChannelUnion voteChannel = event.getOption("vote", OptionMapping::getAsChannel);

                if (inputChannel == null) {
                    event.getHook().editOriginal("The **input** channel can't be found by discord. Try it again later...").queue();
                    return;
                }

                if (voteChannel == null) {
                    event.getHook().editOriginal("The **vote** channel can't be found by discord. Try it again later...").queue();
                    return;
                }

                if (inputChannel.getType() == ChannelType.CATEGORY || voteChannel.getType() == ChannelType.CATEGORY) {
                    event.getHook().editOriginal("One of the inputs (**input**, **vote**) is an category. We can't send messages to this type. Please select an different channel!").queue();
                    return;
                }

                if (this.imageRepo.getBySubmitChannelId(inputChannel.getId()).isPresent()) {
                    event.getHook().editOriginal("More than one rating in a channel isn't allowed!").queue();
                    return;
                }

                ImageRatingDto dto = ImageRatingDto.createDto();
                dto.setServerId(guild.getId());
                dto.setSubmitChannelId(inputChannel.getId());
                dto.setVotingChannelId(voteChannel.getId());
                dto.setState(ImageRatingState.WAITING);

                imageRepo.save(dto);

                event.getHook().editOriginal("Image Rating (**" + dto.getId() + "**) has been created.").queue();

            }
            case "start" -> {

                event.deferReply(true).queue();
                String id = event.getOption("id", OptionMapping::getAsString);
                Role role = event.getOption("mention", OptionMapping::getAsRole);

                if (id == null) {
                    event.getHook().editOriginal("The provided id can't be serialized!").queue();
                    return;
                }

                if (role == null) {
                    event.getHook().editOriginal("The provided role can't be serialized!").queue();
                    return;
                }

                Optional<ImageRatingDto> optional = this.imageRepo.findById(id);

                if (optional.isEmpty()) {
                    event.getHook().editOriginal("Image Rating can't be found by id: " + id).queue();
                    return;
                }

                ImageRatingDto dto = optional.get();
                dto.setState(ImageRatingState.SUBMIT);

                this.imageRepo.save(dto);

                String submitChannelId = dto.getSubmitChannelId();
                TextChannel submitChannel = event.getGuild().getTextChannelById(submitChannelId);

                if (submitChannel == null) {
                    event.getHook().editOriginal("Something went wrong with the submitChannelId: " + submitChannelId).queue();
                    return;
                }

                submitChannel.sendMessage(role.getAsMention())
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Image Rating")
                                        .setDescription("Send your images into this channel and enter the competition!")
                                        .addField("How can i win?", "At first you submit your image here then the community can vote your image and the image with the highest voting gets the price.", false)
                                        .setColor(Color.GREEN)
                                        .setFooter("Event presented by DasShorty")
                                        .build()
                        ).queue(message -> {
                            event.getHook().editOriginal("Message(" + message.getJumpUrl() + ") has been send and rating has started in phase 1!").queue();
                        });

            }
            case "stop" -> {

                //noinspection DuplicatedCode
                event.deferReply(true).queue();
                String id = event.getOption("id", OptionMapping::getAsString);
                GuildChannelUnion resultChannel = event.getOption("result", OptionMapping::getAsChannel);
                Role pingRole = event.getOption("ping", OptionMapping::getAsRole);

                Optional<ImageRatingDto> optional = this.imageRepo.findById(id);

                if (optional.isEmpty()) {
                    event.getHook().editOriginal("Image Rating can't be found by id: " + id).queue();
                    return;
                }

                ImageRatingDto dto = optional.get();

                dto.setState(ImageRatingState.ENDED);

                this.imageRepo.save(dto);

                SubmitDto submitDto = this.choseWinner(dto);

                GuildMessageChannel channel = resultChannel.asGuildMessageChannel();
                channel.sendMessage(pingRole.getAsMention())
                        .addEmbeds(
                                new EmbedBuilder()
                                        .setTitle("Image Rating")
                                        .setDescription("Das Bild von <@" + submitDto.getId() + "> **hat** gewonnen!")
                                        .setImage(this.generateLink(submitDto.getImageId()))
                                        .setColor(Color.GREEN)
                                        .setTimestamp(Instant.now())
                                        .setFooter("Event presented by DasShorty")
                                        .build()
                        ).queue();

            }
            case "voting" -> {

                //noinspection DuplicatedCode
                event.deferReply(true).queue();

                String id = event.getOption("id", OptionMapping::getAsString);

                Optional<ImageRatingDto> optional = this.imageRepo.findById(id);

                if (optional.isEmpty()) {
                    event.getHook().editOriginal("Image Rating can't be found by id: " + id).queue();
                    return;
                }

                ImageRatingDto dto = optional.get();
                dto.setState(ImageRatingState.VOTING);
                List<SubmitDto> list = this.submitRepo.getAllByRatingId(dto.getId());

                String votingChannelId = dto.getVotingChannelId();
                TextChannel textChannelById = guild.getTextChannelById(votingChannelId);

                if (textChannelById == null) {
                    event.getHook().editOriginal("Voting channel can't be found by id: " + votingChannelId).queue();
                    return;
                }

                list.forEach(submitDto -> {

                    this.sendMessage(submitDto, textChannelById);

                });

                event.getHook().editOriginal("Die Votings wurden in den Channel <#" + dto.getVotingChannelId() + "> geschickt.").queue();
            }
        }

    }

    private SubmitDto choseWinner(ImageRatingDto dto) {

        List<SubmitDto> list = this.submitRepo.getAllByRatingId(dto.getId());

        SubmitDto mostDto = null;
        for (SubmitDto submitDto : list) {

            if (mostDto == null) {
                mostDto = submitDto;
            }

            if (mostDto.getRating() < submitDto.getRating()) {
                mostDto = submitDto;
            }

        }

        return mostDto;
    }

    private String generateLink(String imageId) {
        return System.getenv("PROD.URL") + "/image/rating/" + imageId;
    }

    private void sendMessage(SubmitDto submitDto, TextChannel channel) {

        channel.sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Image Rating")
                .setDescription("Dieses Bild stammt von <@" + submitDto.getId() + ">")
                .setImage(this.generateLink(submitDto.getImageId()))
                .build()).addActionRow(
                Button.secondary("image-rating-button_1", "1 Punkt"),
                Button.secondary("image-rating-button_2", "2 Punkte"),
                Button.secondary("image-rating-button_3", "3 Punkte"),
                Button.secondary("image-rating-button_4", "4 Punkte"),
                Button.secondary("image-rating-button_5", "5 Punkte")
        ).queueAfter(new Random().nextInt(5), TimeUnit.SECONDS, message -> {
            submitDto.setMessageId(message.getId());
            this.submitRepo.save(submitDto);
        });

    }
}
