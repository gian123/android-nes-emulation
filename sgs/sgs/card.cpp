#include "card.h"

card::card(string name, CARD_SUIT suit, int num, CARD_TYPE type, CARD_SUB_TYPE subType)
{
	_name = name;
	_suit = suit;
	_num = num;
	_type = type;
	_subType = subType;
}