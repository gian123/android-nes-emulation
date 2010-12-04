#ifndef _CHARA_H_
#define _CHARA_H_

#include "preDeclare.h"

enum SEX
{
	S_MALE,
	S_FEMALE,
};

class skill;

class chara
{
public:
private:
	string _name;
	SEX _sex;
	vector<skill> _vecSkill;
};

#endif // _CHARA_H_