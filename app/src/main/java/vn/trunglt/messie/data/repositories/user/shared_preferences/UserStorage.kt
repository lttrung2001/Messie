package vn.trunglt.messie.data.repositories.user.shared_preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.trunglt.messie.data.repositories.user.shared_preferences.exceptions.CanNotCreateUserException
import vn.trunglt.messie.data.repositories.user.shared_preferences.exceptions.CanNotReadUserException
import vn.trunglt.messie.data.repositories.user.shared_preferences.exceptions.UserNotFoundException
import vn.trunglt.messie.domain.models.UserModel
import java.util.UUID

class UserStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    @Throws(UserNotFoundException::class)
    suspend fun findUser(): UserModel {
        return withContext(ioDispatcher) {
            if (sharedPreferences.contains(USER)) {
                try {
                    sharedPreferences.getString(USER, "")
                        .let { gson.fromJson(it, UserModel::class.java) }
                } catch (e: Exception) {
                    throw CanNotReadUserException()
                }
            } else {
                throw UserNotFoundException()
            }
        }
    }

    @Throws(CanNotCreateUserException::class)
    suspend fun createUser(): UserModel {
        return withContext(ioDispatcher) {
            try {
                val randomName = ANIMAL_NAMES.random()
                val randomId = UUID.randomUUID().toString()
                UserModel(
                    id = randomId,
                    name = randomName,
                ).also {
                    sharedPreferences.edit {
                        putString(USER, gson.toJson(it))
                    }
                }
            } catch (e: Exception) {
                throw CanNotCreateUserException()
            }
        }
    }

    companion object {
        private const val USER = "user"
        private val ANIMAL_NAMES = listOf(
            "Aardvark",
            "Albatross",
            "Alligator",
            "Alpaca",
            "Attack Cat",
            "Ant",
            "Anteater",
            "Antelope",
            "Ape",
            "Armadillo",
            "Donkey",
            "Baboon",
            "Badger",
            "Barracuda",
            "Bat",
            "Bear",
            "Beaver",
            "Bee",
            "Bison",
            "Boar",
            "Buffalo",
            "Butterfly",
            "Camel",
            "Capybara",
            "Caribou",
            "Cat",
            "Caterpillar",
            "Cattle",
            "Chamois",
            "Cheetah",
            "Chicken",
            "Chimpanzee",
            "Chinchilla",
            "Chough",
            "Clam",
            "Cobra",
            "Cockroach",
            "Cod",
            "Cormorant",
            "Coyote",
            "Crab",
            "Crocodile",
            "Crow",
            "Curlew",
            "Deer",
            "Dinosaur",
            "Dog",
            "Dodo",
            "Dolphin",
            "Donkey",
            "Dotterel",
            "Dove",
            "Dragonfly",
            "Duck",
            "Dugong",
            "Dunlin",
            "Eagle",
            "Echidna",
            "Eel",
            "Eland",
            "Elephant",
            "Elk",
            "Emu",
            "Falcon ",
            "Ferret",
            "Finch",
            "Fish",
            "Flamingo",
            "Fly",
            "Fox",
            "Frog",
            "Fowl",
            "Gazelle",
            "Gerbil",
            "Giraffe",
            "Gnat",
            "Goat",
            "Goose",
            "Goldfinch",
            "Gorilla",
            "Goshawk",
            "Grasshopper",
            "Grouse",
            "Guanaco",
            "Gull",
            "Hamster",
            "Hare",
            "Hawk",
            "Hedgehog",
            "Heron",
            "Herring",
            "Hippopotamus",
            "Hornet",
            "Horse",
            "Human",
            "Hummingbird",
            "Hyena",
            "Ibex",
            "Ibis",
            "Jackal",
            "Jaguar",
            "Jay",
            "Jellyfish",
            "Kangaroo",
            "Kingfisher",
            "Koala",
            "Kookaburra",
            "Kouprey",
            "Kudu",
            "Lapwing",
            "Lark",
            "Lemur",
            "Leopard",
            "Lion",
            "Llama",
            "Lobster",
            "Locust",
            "Loris",
            "Louse",
            "Lyrebird",
            "Magpie",
            "Mallard",
            "Manatee",
            "Mandrill",
            "Mantis",
            "Marten",
            "Meerkat",
            "Mink",
            "Mole",
            "Mongoose",
            "Monkey",
            "Moose",
            "Mosquito",
            "Mouse",
            "Mule",
            "Narwhal",
            "Newt",
            "Nightingale",
            "Octopus",
            "Okapi",
            "Opossum",
            "Oryx",
            "Ostrich",
            "Otter",
            "Owl",
            "Oyster",
            "Panther",
            "Parrot",
            "Partridge",
            "Peafowl",
            "Pelican",
            "Penguin",
            "Pheasant",
            "Pig",
            "Pigeon",
            "Pony",
            "Porcupine",
            "Porpoise",
            "Quail",
            "Quelea",
            "Quetzal",
            "Rabbit",
            "Raccoon",
            "Rail",
            "Ram",
            "Rat",
            "Raven",
            "Red deer",
            "Red panda",
            "Reindeer",
            "Rhinoceros",
            "Rook",
            "Salamander",
            "Salmon",
            "Sandpiper",
            "Sardine",
            "Scorpion",
            "Sea lion",
            "Sea urchin",
            "Seal",
            "Shark",
            "Sheep",
            "Shrew",
            "Skunk",
            "Snail",
            "Snake",
            "Sparrow",
            "Spider",
            "Spoonbill",
            "Squid",
            "Squirrel",
            "Starling",
            "Stingray",
            "Stinkbug",
            "Stork",
            "Swallow",
            "Swan",
            "Tapir",
            "Tarsier",
            "Termite",
            "Tiger",
            "Toad",
            "Trout",
            "Turkey",
            "Turtle",
            "Viper",
            "Vulture",
            "Wallaby",
            "Walrus",
            "Wasp",
            "Weasel",
            "Whale",
            "Wildcat",
            "Wolf",
            "Wolverine",
            "Wombat",
            "Woodcock",
            "Woodpecker",
            "Worm",
            "Wren",
            "Yak",
            "Zebra"
        )
    }
}