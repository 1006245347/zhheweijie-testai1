package com.hwj.ai.global

/**
 * @author by jason-何伟杰，2025/2/11
 * des:大模型api-key,这个是deepSeek
 */

const val urlToImageAppIcon =
    "https://res.cloudinary.com/apideck/image/upload/v1672442492/marketplaces/ckhg56iu1mkpc0b66vj7fsj3o/listings/-4-ans_frontend_assets.images.poe.app_icon.png-26-8aa0a2e5f237894d_tbragv.png"
const val urlToImageAuthor = "https://avatars.githubusercontent.com/u/24426708?v=4"
const val urlToAvatarGPT = "https://avatars.githubusercontent.com/u/148330874?s=200&v=4"
const val urlToAuthor =
    "https://blog.csdn.net/j7a2son/article/details/145931038?spm=1001.2101.3001.6650.3&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogOpenSearchComplete%7ECtr-3-145931038-blog-147047202.235%5Ev43%5Econtrol&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogOpenSearchComplete%7ECtr-3-145931038-blog-147047202.235%5Ev43%5Econtrol&utm_relevant_index=3"

const val conversationTestTag = "ConversationTestTag"

const val logTAG = "yuy"
const val thinkingZw = "思考中..."
//const val thinking = "thinking..."
const val thinking = "思考中"
const val stopAnswer="已停止生成"
const val answerUseEw = ",\nPlease answer in English."
const val answerUseZw = ",\n请用中文回答。"
const val translateEw = "如果以下内容是英文，将内容翻译为中文，如果以下内容不是英文，将内容翻译成英文，保持专业术语准确性，不要添加额外内容："
//const val translateZw = "将以下内容翻译为中文，保持专业术语准确性，不要添加额外内容："

//数据
const val DATA_USER_NAME = "User"
const val DATA_SYSTEM_NAME = "BOT"
const val DATA_APP_TOKEN = "DATA_APP_TOKEN"
const val DATA_IMAGE_TITLE = "识别图片信息,"
const val DATA_MESSAGE_TAG = "DATA_MESSAGE_TAG"
const val DATA_CONVERSATION_TAG = "DATA_CONVERSATION_TAG"
const val DATA_USER_ID = "DATA_USER_ID"

//标识
const val CODE_IS_DARK = "CODE_IS_DARK" //是否黑暗模式
const val CODE_LANGUAGE_ZH = "CODE_LANGUAGE_ZH"//语言是否是中文
const val CODE_HOT_KEY = "CODE_HOT_KEY" //alt+B快捷键
const val CODE_SELECTION_USE = "CODE_SELECTION_USE"

//联网搜索,博查api
const val DATA_ZH_WEB_SEARCH = "\\\n" +
        "'''# 以下内容是基于用户发送的消息的搜索结果:\n" +
        "{search_results}\n" +
        "在我给你的搜索结果中，每个结果都是[webpage X begin]...[webpage X end]格式的，X代表每篇文章的数字索引。请在适当的情况下在句子末尾引用上下文。请按照引用编号[citation:X]的格式在答案中对应部分引用上下文。如果一句话源自多个上下文，请列出所有相关的引用编号，例如[citation:3][citation:5]，切记不要将引用集中在最后返回引用编号，而是在答案对应部分列出。\n" +
        "在回答时，请注意以下几点：\n" +
        "- 今天是{cur_date}。\n" +
        "- 并非搜索结果的所有内容都与用户的问题密切相关，你需要结合问题，对搜索结果进行甄别、筛选。\n" +
        "- 对于列举类的问题（如列举所有航班信息），尽量将答案控制在10个要点以内，并告诉用户可以查看搜索来源、获得完整信息。优先提供信息完整、最相关的列举项；如非必要，不要主动告诉用户搜索结果未提供的内容。\n" +
        "- 对于创作类的问题（如写论文），请务必在正文的段落中引用对应的参考编号，例如[citation:3][citation:5]，不能只在文章末尾引用。你需要解读并概括用户的题目要求，选择合适的格式，充分利用搜索结果并抽取重要信息，生成符合用户要求、极具思想深度、富有创造力与专业性的答案。你的创作篇幅需要尽可能延长，对于每一个要点的论述要推测用户的意图，给出尽可能多角度的回答要点，且务必信息量大、论述详尽。\n" +
        "- 如果回答很长，请尽量结构化、分段落总结。如果需要分点作答，尽量控制在5个点以内，并合并相关的内容。\n" +
        "- 对于客观类的问答，如果问题的答案非常简短，可以适当补充一到两句相关信息，以丰富内容。\n" +
        "- 你需要根据用户要求和回答内容选择合适、美观的回答格式，确保可读性强。\n" +
        "- 你的回答应该综合多个相关网页来回答，不能重复引用一个网页。\n" +
        "- 除非用户要求，否则你回答的语言需要和用户提问的语言保持一致。\n" +
        "\n" +
        "# 用户消息为：\n" +
        "{question}'''"

const val DATA_EN_WEB_SEARCH = "\\\n" +
        "'''# The following contents are the search results related to the user's message:\n" +
        "{search_results}\n" +
        "In the search results I provide to you, each result is formatted as [webpage X begin]...[webpage X end], where X represents the numerical index of each article. Please cite the context at the end of the relevant sentence when appropriate. Use the citation format [citation:X] in the corresponding part of your answer. If a sentence is derived from multiple contexts, list all relevant citation numbers, such as [citation:3][citation:5]. Be sure not to cluster all citations at the end; instead, include them in the corresponding parts of the answer.\n" +
        "When responding, please keep the following points in mind:\n" +
        "- Today is {cur_date}.\n" +
        "- Not all content in the search results is closely related to the user's question. You need to evaluate and filter the search results based on the question.\n" +
        "- For listing-type questions (e.g., listing all flight information), try to limit the answer to 10 key points and inform the user that they can refer to the search sources for complete information. Prioritize providing the most complete and relevant items in the list. Avoid mentioning content not provided in the search results unless necessary.\n" +
        "- For creative tasks (e.g., writing an essay), ensure that references are cited within the body of the text, such as [citation:3][citation:5], rather than only at the end of the text. You need to interpret and summarize the user's requirements, choose an appropriate format, fully utilize the search results, extract key information, and generate an answer that is insightful, creative, and professional. Extend the length of your response as much as possible, addressing each point in detail and from multiple perspectives, ensuring the content is rich and thorough.\n" +
        "- If the response is lengthy, structure it well and summarize it in paragraphs. If a point-by-point format is needed, try to limit it to 5 points and merge related content.\n" +
        "- For objective Q&A, if the answer is very brief, you may add one or two related sentences to enrich the content.\n" +
        "- Choose an appropriate and visually appealing format for your response based on the user's requirements and the content of the answer, ensuring strong readability.\n" +
        "- Your answer should synthesize information from multiple relevant webpages and avoid repeatedly citing the same webpage.\n" +
        "- Unless the user requests otherwise, your response should be in the same language as the user's question.\n" +
        "\n" +
        "# The user's message is:\n" +
        "{question}'''"