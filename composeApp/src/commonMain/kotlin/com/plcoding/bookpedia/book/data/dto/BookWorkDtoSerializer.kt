package com.plcoding.bookpedia.book.data.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

//Ovaj serializer će biti korišćen za prilagođeno parsiranje BookWorkDto objekata.
//Podržava oba formata ("description": "Tekst" i "description": { "value": "Tekst" }).
object BookWorkDtoSerializer: KSerializer<BookWorkDto> {

    //SerialDescriptor opisuje strukturu podataka za Kotlinx Serialization.
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(BookWorkDto::class.simpleName!!){

        element<String?>("description")
    }

    //Ova metoda pretvara JSON u BookWorkDto objekat.
    override fun deserialize(decoder: Decoder): BookWorkDto = decoder.decodeStructure(descriptor){

        var description: String? = null

        while(true){
            //decodeElementIndex(descriptor) se koristi za iteraciju kroz JSON polja.
            when(val index = decodeElementIndex(descriptor)){
                0 -> {
                    //Proverava da li je decoder JSON dekoder.
                    val jsonDecoder = decoder as? JsonDecoder?: throw SerializationException("This decoder only works with JSON.")
                    //decodeJsonElement() uzima sirovi JSON element (bilo da je String, JsonObject, itd.).
                    val element = jsonDecoder.decodeJsonElement()
                    //Ako je element JSON objekat, koristi DescriptionDto da izvuče "value".
                    description = if(element is JsonObject){
                        decoder.json.decodeFromJsonElement<DescriptionDto>(
                            element = element,
                            deserializer = DescriptionDto.serializer()
                        ).value
                        //Ako je element običan string, postavlja njegov sadržaj kao description.
                    }else if(element is JsonPrimitive && element.isString){
                        element.content
                    }else{
                        null
                    }
                }
                CompositeDecoder.DECODE_DONE -> break
                else -> throw SerializationException("Unexpected index $index")
            }
        }
        //Kreira i vraća BookWorkDto objekat sa izvučenim description poljem.
        return@decodeStructure BookWorkDto(description)
    }

    override fun serialize(encoder: Encoder, value: BookWorkDto) = encoder.encodeStructure(
        descriptor){
        value.description?.let{
            encodeStringElement(descriptor,0, it)
        }
    }
}