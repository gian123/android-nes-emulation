#ifndef _CARD_H_
#define _CARD_H_

#include "preDeclare.h"

enum CARD_SUIT
{
	CS_HEART,		// 红
	CS_CLUB,		// 梅
	CS_DIAMOND,		// 方
	CS_SPADE,		// 黑
};

enum CARD_TYPE
{
	CT_TRICK,		// 锦囊牌
	CT_DELAY_TRICK, // 延迟锦囊牌
	CT_EQUIPMENT,	// 装备牌
	CT_NORMAL,		// 普通牌
};

enum CARD_SUB_TYPE
{
	// 锦囊
	CST_WXKJ,	// 无懈可击
	CST_SSQY,	// 顺手牵羊
	CST_WZSY,	// 无中生有
	CST_WGFD,	// 五谷丰登

	// 延时锦囊
	CST_SD,		// 闪电
	CST_LBSS,	// 乐不思蜀

	// 装备
	CST_BGZ, // 八卦阵
	CST_QLD, // 青龙偃月刀
	CST_DH,  // +1马
	CST_OH,  // -1马

	// 普通
	CST_SHA,  // 闪
	CST_SHAN, // 杀
	CST_TAO,  // 桃
};



class card
{
public:
	card(string name, CARD_SUIT suit, int num, CARD_TYPE type, CARD_SUB_TYPE subType);

	virtual void operate() = 0;
protected:
private:
	CARD_SUIT _suit;
	CARD_TYPE _type;
	CARD_SUB_TYPE _subType;
	int _num;
	string _name;
	bool _isTmp;
};

#endif // _CARD_H_